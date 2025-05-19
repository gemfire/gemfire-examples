// Copyright (c) VMware, Inc. 2025.
// All rights reserved. SPDX-License-Identifier: Apache-2.0

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vmware.gemfire.examples.transactionsMixedWithNon.repair;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.partition.PartitionRegionHelper;

/**
 * Function that searches any entries older than a certain age. If it finds one, it
 * will check to see if that entry exists in the primary. If the primary does not
 * contain the entry, the key will be returned from the function.
 * <p>
 * This function can be invoked through gfsh. The first parameter is the region
 * name to examine. The second parameter the age in minutes of the entries. Only
 * entries with a last modified older than this age will be considered.
 * <p>
 * Example of finding entries older than 1 hour and checking them.
 * <p>
 * <code>
 *   execute function --id=FindOldEntriesFunction --arguments=example-region,60
 * </code>
 */
public class FindOldEntriesFunction implements Function<String[]> {
  public static final String ID = FindOldEntriesFunction.class.getSimpleName();
  private static final int LIMIT = 1000;


  @Override
  public void execute(FunctionContext<String[]> context) {

    String[] args = context.getArguments();

    String regionName = args[0];
    int minutesOld = Integer.parseInt(args[1]);

    Region<Object, Object> region = context.getCache().getRegion(regionName);
    Region<?, ?> localData =
        PartitionRegionHelper.getLocalData(region);

    long age = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(minutesOld);
    int count = 0;
    StringBuilder oldEntries = new StringBuilder();
    for (Region.Entry<?, ?> entry : localData.entrySet(false)) {
      if (entry.getStatistics().getLastModifiedTime() < age) {
        Object key = entry.getKey();
        List<Boolean> isInPrimary =
            (List<Boolean>) FunctionService.onRegion(region)
                .withFilter(Collections.singleton(key))
                .execute(ContainsKeyOnPrimaryFunction.ID).getResult();

        if (!isInPrimary.stream().allMatch(Boolean::booleanValue)) {
          oldEntries.append("Not found in primary: ").append(key).append("\n");
          if (count++ > LIMIT) {
            break;
          } ;
        }
      }
    }

    if (oldEntries.length() == 0) {
      oldEntries.append("No old entries");
    }
    if (count >= LIMIT) {
      oldEntries.append("Found too many entries to check. Hit the limit of " + LIMIT);
    }
    context.getResultSender().lastResult(oldEntries.toString());
  }

  @Override
  public String getId() {
    return ID;
  }
}
