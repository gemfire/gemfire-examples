// Copyright 2023-2024 Broadcom. All rights reserved.

package dev.gemfire.stat.logger;

import org.apache.geode.StatisticDescriptor;
import org.apache.geode.Statistics;

public class StatsHolder {

  private Statistics statistics;
  private StatisticDescriptor statisticDescriptor;
  private String prettyName;

  public StatsHolder(String prettyName, Statistics statistics,
      StatisticDescriptor statisticDescriptor) {
    this.prettyName = prettyName;
    this.statistics = statistics;
    this.statisticDescriptor = statisticDescriptor;
  }

  @Override
  public String toString() {
    return prettyName + " = " + statistics.get(statisticDescriptor);
  }
}
