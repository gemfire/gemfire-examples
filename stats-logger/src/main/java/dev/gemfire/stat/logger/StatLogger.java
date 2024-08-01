// Copyright 2023-2024 Broadcom. All rights reserved.

package dev.gemfire.stat.logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import org.apache.geode.StatisticDescriptor;
import org.apache.geode.Statistics;
import org.apache.geode.StatisticsType;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.RegionFactory;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.distributed.DistributedSystem;
import org.apache.geode.logging.internal.log4j.api.LogService;

public class StatLogger implements Function, Declarable {
  private static final Logger logger = LogService.getLogger();
  private List<StatsHolder> listOfStats = new CopyOnWriteArrayList<>();
  private Timer timer;
  private TimerTask task;
  private long timerInterval = 5000;

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
    String logFrequency = properties.get("logFrequency").toString();
    String statistics = properties.getProperty("statistics");

    if (StringUtils.isEmpty(statistics) && StringUtils.isEmpty(logFrequency)) {
      functionContext.getResultSender()
          .lastResult("No properties set for StatLogger. StatLogger will not start.");
    } else if (!StringUtils.isEmpty(statistics) && !StringUtils.isEmpty(logFrequency)) {
      RegionFactory<Object, Object> regionFactory =
              functionContext.getCache().createRegionFactory(RegionShortcut.LOCAL);
      regionFactory.create("Timer");
      logger.info("Created Timer region");

      listOfStats.clear();
      timerInterval = Long.parseLong(logFrequency);

      List<String> statisticsList = Arrays.asList(statistics.split(",", 0));
      for (String statistic : statisticsList) {
        String[] statsNames = statistic.split("[.]", 0);
        addStats(functionContext.getCache().getDistributedSystem(), statsNames[0], statsNames[1]);
      }

      cancelTimer();
      ensureTimerRunning(functionContext.getCache());
      functionContext.getResultSender()
          .lastResult("Logging Metric count " + listOfStats.size() + " with timer interval set to "
              + timerInterval + " ms");
    } else if (StringUtils.isEmpty(statistics)) {
      cancelTimer();
      functionContext.getResultSender().lastResult("No stats to log. StatLogger will not start.");
    }
  }

  private void cancelTimer() {
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
  }

  private void ensureTimerRunning(Cache cache) {
    if (timer == null && !listOfStats.isEmpty()) {
      timer = new Timer("StatLogger", true);
      task = new TimerTask() {
        @Override
        public void run() {
          for (StatsHolder curr : listOfStats) {
            logger.info(curr.toString());
          }
        }
      };
      timer.scheduleAtFixedRate(task, 0, timerInterval);
      logger.info("StatLogger timer started");
      cache.getRegion("Timer").put("statsLoggerTimer", timer);
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
