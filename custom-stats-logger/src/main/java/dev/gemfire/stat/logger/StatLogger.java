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

public class StatLogger implements Function {

  private String result;
  private Timer timer;
  private long timerInterval = 5000;

  private static final String REGION = "GemFireStatLoggerTimer";
  private static final String STATS_LOGGER_TIMER_REGION_KEY = "statsLoggerTimer";
  private static final String STAT_LOGGER_THREAD_NAME = "StatLogger";
  private static final String LOG_FREQUENCY_PROPERTY_NAME = "logFrequency";
  private static final String STATISTICS_PROPERTY_NAME = "statistics";
  private static final Logger LOGGER = LogService.getLogger();


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
    Properties properties = getProperties();

    String logFrequency = properties.getProperty(LOG_FREQUENCY_PROPERTY_NAME);
    String statistics = properties.getProperty(STATISTICS_PROPERTY_NAME);

    cancelTimer(functionContext.getCache());

    if (!StringUtils.isEmpty(statistics) && !StringUtils.isEmpty(logFrequency)) {
      createTimerRegion(functionContext.getCache());

      timerInterval = Long.parseLong(logFrequency);
      List<StatsHolder> statsList = createStatsList(functionContext.getCache().getDistributedSystem(), statistics);
      createTimer(functionContext.getCache(), statsList);
      result = "Logging Metric count " + statsList.size() + " with timer interval set to " + timerInterval + " ms";

    } else if (StringUtils.isEmpty(statistics) && !StringUtils.isEmpty(logFrequency)) {
      result = "No stats to log. StatLogger will not start.";

    } else if (!StringUtils.isEmpty(statistics) && StringUtils.isEmpty(logFrequency)) {
      createTimerRegion(functionContext.getCache());

      List<StatsHolder> statsList = createStatsList(functionContext.getCache().getDistributedSystem(), statistics);
      createTimer(functionContext.getCache(), statsList);
      result = "Logging Metric count " + statsList.size() + " with timer interval set to default of " + timerInterval + " ms";

    } else {
      result = "No properties set for StatLogger. StatLogger will not start.";

    }

    functionContext.getResultSender().lastResult(result);
  }

  private Properties getProperties() {
    ClassLoader contextClassLoader = this.getClass().getClassLoader();
    Properties properties = new Properties();
    try {
      final InputStream stream = contextClassLoader.getResourceAsStream("statLogger.properties");
      properties.load(stream);
    } catch (IOException e) {
      LOGGER.error("Could not load statLogger.properties", e);
    }
    return properties;
  }

  private static void createTimerRegion(Cache cache) {
    RegionFactory<Object, Object> regionFactory = cache.createRegionFactory(RegionShortcut.LOCAL);
    regionFactory.create(REGION);
    LOGGER.info("Created {} region", REGION);
  }

  private void cancelTimer(Cache cache) {
    Region<Object, Object> timerRegion = cache.getRegion(REGION);
    if (timerRegion != null) {
      Object statsLoggerTimer = timerRegion.get(STATS_LOGGER_TIMER_REGION_KEY);
      if (statsLoggerTimer != null) {
        ((Timer) statsLoggerTimer).cancel();
        LOGGER.info("Stopped old {}", STATS_LOGGER_TIMER_REGION_KEY);
        timerRegion.destroyRegion();
        LOGGER.info("Destroyed {} region", REGION);
      }
    }
  }

  private void createTimer(Cache cache, List<StatsHolder> statsList) {
    if (timer == null && !statsList.isEmpty()) {
      timer = new Timer(STAT_LOGGER_THREAD_NAME, true);
      timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          for (StatsHolder curr : statsList) {
            LOGGER.info(curr.toString());
          }
        }
      }, 0, timerInterval);
      LOGGER.info("{} has started", STAT_LOGGER_THREAD_NAME);
      cache.getRegion(REGION).put(STATS_LOGGER_TIMER_REGION_KEY, timer);
    }
  }

  private List<StatsHolder> createStatsList(DistributedSystem distributedSystem, String statistics) {
    List<StatsHolder> statsHolderList = new ArrayList<>();
    List<String> statisticsList = Arrays.asList(statistics.split(",", 0));
    for (String statistic : statisticsList) {
      String[] statsNames = statistic.split("[.]", 0);
      statsHolderList = updateEachStatList(distributedSystem, statsNames[0], statsNames[1], statsHolderList);
    }
    return statsHolderList;
  }

  private List<StatsHolder> updateEachStatList(DistributedSystem ds, String groupName, String statName, List<StatsHolder> listOfStats) {
    StatisticsType type = ds.findType(groupName);
    for (Statistics currStatistics : ds.findStatisticsByType(type)) {
      if (currStatistics.getTextId().compareToIgnoreCase(groupName) == 0) {
        for (StatisticDescriptor currDescriptor : type.getStatistics()) {
          if (currDescriptor.getName().compareToIgnoreCase(statName) == 0) {
            listOfStats.add(new StatsHolder(groupName + "." + statName, currStatistics, currDescriptor));
          }
        }
      }
    }
    return listOfStats;
  }
}
