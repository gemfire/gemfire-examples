// Copyright (c) VMware, Inc. 2025.
// All rights reserved. SPDX-License-Identifier: Apache-2.0

package com.vmware.gemfire.examples.transactionsMixedWithNon.repair;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.internal.cache.BucketRegion;
import org.apache.geode.internal.cache.PartitionedRegion;

/**
 * Function that removes an entry from every copy of the bucket on a partitioned region
 * <p>
 * This function is intended to repair situations where an entry exists only in the
 * secondary copies of a bucket, so the entry cannot be removed using remove entry.
 * <p>
 * This function can be invoked through gfsh. The first parameter is the region
 * name. The second parameter the the key to remove. This function assumes the keys are strings
 * <p>
 * Example of removing a single entry with the key "John"
 * <p>
 * <code>
 *   execute function --id=RemoveMismatchedKeysFromSecondaryFunction --arguments=example-region,John
 * <code>
 */
public class RemoveMismatchedKeysFromSecondaryFunction implements Function<String[]> {
  public static final String ID = RemoveMismatchedKeysFromSecondaryFunction.class.getSimpleName();

  @Override
  public void execute(FunctionContext<String[]> context) {
    final String[] args = context.getArguments();
    String regionName = args[0];
    String mismatchedKey = args[1];
    PartitionedRegion pr = (PartitionedRegion) context.getCache().getRegion(regionName);

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

  @Override
  public String getId() {
    return ID;
  }
}
