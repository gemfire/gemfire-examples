// Copyright (c) VMware, Inc. 2025.
// All rights reserved. SPDX-License-Identifier: Apache-2.0

package com.vmware.gemfire.examples.transactionsMixedWithNon.repair;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.internal.cache.BucketRegion;
import org.apache.geode.internal.cache.PartitionedRegion;

public class RemoveMismatchedKeysFromSecondaryFunction implements Function<Object[]> {
  @Override
  public void execute(FunctionContext<Object[]> context) {
    final Object[] args = context.getArguments();
    String regionName = (String) args[0];
    String mismatchedKey = (String) args[1];
    PartitionedRegion pr = (PartitionedRegion) context.getCache().getRegion(regionName);

    // System.out.println("In RemoveMismatchedKeysFromSecondaryFunction:" + args[0]+":"+args[1]);
    for (int bucketId : pr.getDataStore().getAllLocalBucketIds()) {
      BucketRegion br = pr.getDataStore().getLocalBucketById(bucketId);
      if (br.containsKey(mismatchedKey)) {
        br.destroyRecoveredEntry(mismatchedKey);
      }
    }
    context.getResultSender().lastResult("OK");
  }

  @Override
  public boolean isHA() {
    return false;
  }
}
