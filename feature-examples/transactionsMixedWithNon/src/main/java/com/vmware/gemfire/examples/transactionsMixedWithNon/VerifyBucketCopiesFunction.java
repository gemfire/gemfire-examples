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
package com.vmware.gemfire.examples.transactionsMixedWithNon;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.internal.cache.BucketDump;
import org.apache.geode.internal.cache.ForceReattemptException;
import org.apache.geode.internal.cache.PartitionedRegion;

/**
 * Function that searches for any entries that do not exist in all copies of a bucket.
 * <p/>
 * This function should only be invoked when the system is quiet. If there are in progress
 * updates they may be erroneously flagged by this function.
 * <p/>
 * This function requires some memory - it pulls a full copy of each bucket from each member
 * to the member running this function.
 * <p/>
 * This function should be invoked with FunctionService.onMember or FunctionService.onServer. This
 * function will validate all buckets for the configured region.
 */
public class VerifyBucketCopiesFunction implements Function {
  public static final String ID = VerifyBucketCopiesFunction.class.getSimpleName();

  @Override
  public void execute(FunctionContext context) {
    String[] args = (String[]) context.getArguments();

    String regionName = args[0];
    PartitionedRegion region = (PartitionedRegion) context.getCache().getRegion(regionName);
    StringBuilder failure = new StringBuilder();
    for (int i = 0; i < region.getPartitionAttributes().getTotalNumBuckets(); i++) {
      List<BucketDump> dumps = getBucketDumps(region, i);
      BucketDump dumpA = dumps.get(0);
      for (BucketDump dumpB : dumps) {
        if (!dumpA.getValues().equals(dumpB.getValues())) {
          Map<Object, Object> valuesInA = dumpA.getValues();
          Map<Object, Object> valuesInB = dumpB.getValues();
          DistributedMember primaryMember = region.getBucketPrimary(i);

          failure.append(
              String.format("Data does not match for bucket %s. Primary is %s\n", i,
                  primaryMember));

          Set<Object> allKeys = new HashSet<>(valuesInA.keySet());
          allKeys.addAll(valuesInB.keySet());

          for (Object key : allKeys) {
            Object aValue = valuesInA.get(key);
            Object bValue = valuesInB.get(key);
            if (!Objects.equals(aValue, bValue)) {
              failure.append(
                  String.format("Inconsistency in key %s. Value in %s is %s. Value in %s is %s\n",
                      key, dumpA.getMember(), aValue, dumpB.getMember(), bValue));
            }
          }
        }
      }
    }

    if (failure.length() != 0) {
      throw new RuntimeException(failure.toString());
    }
    context.getResultSender().lastResult("done");
  }

  private static List<BucketDump> getBucketDumps(PartitionedRegion region, int i) {
    List<BucketDump> dumps;
    try {
      dumps = region.getAllBucketEntries(i);
    } catch (ForceReattemptException e) {
      throw new RuntimeException(e);
    }
    return dumps;
  }

  @Override
  public String getId() {
    return ID;
  }

}
