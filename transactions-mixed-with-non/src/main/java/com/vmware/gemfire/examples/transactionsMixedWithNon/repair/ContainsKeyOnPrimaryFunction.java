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

import java.util.Set;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;

/**
 * Internal function used by {@link FindOldEntriesFunction}. This
 * returns true if the entry passed in as it's filter exists
 * on this member.
 */
public class ContainsKeyOnPrimaryFunction implements Function {
  public static final String ID = ContainsKeyOnPrimaryFunction.class.getSimpleName();
  private static final int LIMIT = 1000;


  @Override
  public void execute(FunctionContext context) {
    RegionFunctionContext rfc = (RegionFunctionContext) context;

    Set<?> keys = ((RegionFunctionContext<?>) context).getFilter();

    // Function is always called with a single key as the filter
    Object key = keys.iterator().next();

    Region<?, ?> localData = PartitionRegionHelper.getLocalDataForContext(rfc);

    context.getResultSender().lastResult(localData.containsKey(key));
  }

  @Override
  public boolean optimizeForWrite() {
    return true;
  }

  @Override
  public String getId() {
    return ID;
  }
}
