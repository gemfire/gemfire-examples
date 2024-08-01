// Copyright 2023-2024 Broadcom. All rights reserved.

package dev.gemfire.stat.logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.geode.StatisticDescriptor;
import org.apache.geode.Statistics;
import org.apache.geode.StatisticsType;
import org.apache.geode.cache.*;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.distributed.DistributedSystem;
import org.apache.geode.logging.internal.log4j.api.LogService;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class StatLogger implements Function {

  private List<StatsHolder> listOfStats = new CopyOnWriteArrayList<>();
  private Timer timer;
  private TimerTask task;
  private long timerInterval = 5000;
  private static final String region = "GemFireStatLoggerTimer";
  private static final String statsLoggerTimerRegionKey = "statsLoggerTimer";
  private static final String statLoggerThreadName = "StatLogger";
  private static final Logger logger = LogService.getLogger();

  @Override
  public boolean isHA() {
    return false;
  }

  @Override
  public boolean hasResult() {
    return true;
  }

  @Override
  public String getId() {
    return StatLogger.class.getSimpleName();
  }

  @Override
  public void execute(FunctionContext functionContext) {
    ClassLoader contextClassLoader = this.getClass().getClassLoader();
    Properties properties = new Properties();
    try {
      final InputStream stream = contextClassLoader.getResourceAsStream("statLogger.properties");
      properties.load(stream);
    } catch (IOException e) {
      logger.error("Could not load statLogger.properties", e);
    }
    String logFrequency = properties.getProperty("logFrequency");
    String statistics = properties.getProperty("statistics");

    cleanup(functionContext.getCache());

    if (!StringUtils.isEmpty(statistics) && !StringUtils.isEmpty(logFrequency)) {
      createTimerRegion(functionContext.getCache());

      timerInterval = Long.parseLong(logFrequency);
      createStatsList(functionContext.getCache().getDistributedSystem(), statistics);
      createTimer(functionContext.getCache());
      functionContext.getResultSender().lastResult("Logging Metric count " + listOfStats.size() +
              " with timer interval set to " + timerInterval + " ms");

    } else if (StringUtils.isEmpty(statistics) && !StringUtils.isEmpty(logFrequency)) {
      functionContext.getResultSender().lastResult("No stats to log. StatLogger will not start.");

    } else if (!StringUtils.isEmpty(statistics) && StringUtils.isEmpty(logFrequency)) {
      createTimerRegion(functionContext.getCache());

      createStatsList(functionContext.getCache().getDistributedSystem(), statistics);
      createTimer(functionContext.getCache());
      functionContext.getResultSender().lastResult("Logging Metric count " + listOfStats.size() +
              " with timer interval set to default of " + timerInterval + " ms");

    } else {
      functionContext.getResultSender().lastResult("No properties set for StatLogger. StatLogger will not start.");

    }
  }

  private void createStatsList(DistributedSystem distributedSystem, String statistics) {
    List<String> statisticsList = Arrays.asList(statistics.split(",", 0));
    for (String statistic : statisticsList) {
      String[] statsNames = statistic.split("[.]", 0);
      addStats(distributedSystem, statsNames[0], statsNames[1]);
    }
  }

  private static void createTimerRegion(Cache cache) {
    RegionFactory<Object, Object> regionFactory = cache.createRegionFactory(RegionShortcut.LOCAL);
    regionFactory.create(region);
    logger.info("Created {} region", region);
  }

  private void cleanup(Cache cache) {
    cancelTimer(cache);
    listOfStats.clear();
  }

  private void cancelTimer(Cache cache) {
    Region<Object, Object> timerRegion = cache.getRegion(region);
    if (timerRegion != null) {
      Object statsLoggerTimer = timerRegion.get(statsLoggerTimerRegionKey);
      if (statsLoggerTimer != null) {
        ((Timer) statsLoggerTimer).cancel();
        logger.info("Stopped old {}", statsLoggerTimerRegionKey);
        timerRegion.destroyRegion();
        logger.info("Destroyed {} region", region);
      }
    }
  }

  private void createTimer(Cache cache) {
    if (timer == null && !listOfStats.isEmpty()) {
      timer = new Timer(statLoggerThreadName, true);
      task = new TimerTask() {
        @Override
        public void run() {
          for (StatsHolder curr : listOfStats) {
            logger.info(curr.toString());
          }
        }
      };
      timer.scheduleAtFixedRate(task, 0, timerInterval);
      logger.info("{} has started", statLoggerThreadName);
      cache.getRegion(region).put(statsLoggerTimerRegionKey, timer);
    }
  }

  private void addStats(DistributedSystem ds, String groupName, String statName) {
    StatisticsType type = ds.findType(groupName);
    for (Statistics currStatistics : ds.findStatisticsByType(type)) {
      if (currStatistics.getTextId().compareToIgnoreCase(groupName) == 0) {
        for (StatisticDescriptor currDescriptor : type.getStatistics()) {
          if (currDescriptor.getName().compareToIgnoreCase(statName) == 0) {
            listOfStats
                .add(new StatsHolder(groupName + "." + statName, currStatistics, currDescriptor));
          }
        }
      }
    }
  }
}
