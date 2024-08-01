// Copyright 2023-2024 Broadcom. All rights reserved.

package dev.gemfire.stat.logger;

import dev.gemfire.stat.exception.GemfireStatNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.apache.geode.*;
import org.apache.geode.cache.*;
import org.apache.geode.cache.client.internal.Op;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.distributed.DistributedSystem;
import org.apache.geode.logging.internal.log4j.api.LogService;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class StatLogger implements Function {

  private static final String DEFAULT_LOG_FREQUENCY = "5000";
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
    String logFrequency = properties.getProperty(LOG_FREQUENCY_PROPERTY_NAME, DEFAULT_LOG_FREQUENCY);
    String statistics = properties.getProperty(STATISTICS_PROPERTY_NAME);

    if (StringUtils.isEmpty(statistics)) {
      cancelTimer(functionContext.getCache());
      functionContext.getResultSender().lastResult("No properties set for StatLogger. StatLogger will not start.");
      return;
    }

    long timerInterval = Long.parseLong(logFrequency);
    createTimerRegion(functionContext.getCache());
    try {
      List<StatsHolder> statsHolderList = createStatsList(functionContext.getCache().getDistributedSystem(), statistics);
      createTimer(functionContext.getCache(), statsHolderList, timerInterval);
      functionContext.getResultSender().lastResult("Logging Metric count " + statsHolderList.size() + " with timer interval set to " + timerInterval + " ms");
    }
    catch (GemfireStatNotFoundException exception) {
      functionContext.getResultSender().sendException(exception);
    }
  }

  private String formatStatsNotFoundList(List<String> statsNotFoundList) {
    return "Stats not found for " + StringUtils.join(statsNotFoundList, ",");
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
    if (cache.getRegion(REGION) == null) {
      RegionFactory<Object, Object> regionFactory = cache.createRegionFactory(RegionShortcut.LOCAL);
      regionFactory.create(REGION);
      LOGGER.info("Created {} region", REGION);
    }
  }

  private void cancelTimer(Cache cache) {
    Region<Object, Object> timerRegion = cache.getRegion(REGION);
    if (timerRegion != null) {
      Object statsLoggerTimer = timerRegion.remove(STATS_LOGGER_TIMER_REGION_KEY);
      if (statsLoggerTimer != null) {
        ((Timer) statsLoggerTimer).cancel();
        LOGGER.info("Stopped old {}", STATS_LOGGER_TIMER_REGION_KEY);
      }
    }
  }

  private void createTimer(Cache cache, List<StatsHolder> statsList, long timerInterval) {
    Timer timer = (Timer) cache.getRegion(REGION).get(STATS_LOGGER_TIMER_REGION_KEY);
    if (timer != null) {
      cancelTimer(cache);
    }
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

  private List<StatsHolder> createStatsList(DistributedSystem distributedSystem, String statistics) {
    List<StatsHolder> statsHolderList = new ArrayList<>();
    List<String> notFoundStatsList = new ArrayList<>();
    List<String> statisticsList = Arrays.asList(statistics.split(",", 0));
    for (String statistic : statisticsList) {
      String[] statsNames = statistic.split("[.]", 0);
      Optional<StatsHolder> statsHolder = updateEachStatList(distributedSystem, statsNames[0], statsNames[1]);
      if (statsHolder.isPresent()) {
        statsHolderList.add(statsHolder.get());
      }
      else {
        notFoundStatsList.add( statsNames[0] + "." +  statsNames[1]);
      }
    }
    if (!notFoundStatsList.isEmpty()) {
      throw new GemfireStatNotFoundException(formatStatsNotFoundList(notFoundStatsList));
    }
    return statsHolderList;
  }

  private Optional<StatsHolder> updateEachStatList(DistributedSystem distributedSystem, String groupName, String statName) {
    StatisticsType type = distributedSystem.findType(groupName);
    for (Statistics currStatistics : distributedSystem.findStatisticsByType(type)) {
      if (currStatistics.getTextId().compareToIgnoreCase(groupName) == 0) {
        for (StatisticDescriptor currDescriptor : type.getStatistics()) {
          if (currDescriptor.getName().compareToIgnoreCase(statName) == 0) {
            return Optional.of(new StatsHolder(groupName + "." + statName, currStatistics, currDescriptor));
          }
        }
      }
    }
      return Optional.empty();
  }
}
