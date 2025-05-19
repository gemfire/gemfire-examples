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
package com.vmware.gemfire.examples.transactionsMixedWithNon.reproduce;

import java.util.AbstractQueue;
import java.util.HashSet;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;

public class FindAndUpdateEntriesFunction implements Function {
  public static final String ID = FindAndUpdateEntriesFunction.class.getSimpleName();

  @Override
  public void execute(FunctionContext context) {
    RegionFunctionContext rfc = (RegionFunctionContext) context;

    Region dataSet = rfc.getDataSet();
    HashSet<Object> toReturn = new HashSet<>(dataSet.keySet());
    for (Object key : toReturn) {
      //Non-transactional update. Comment these two lines out and use the transactional
      //update below instead to prevent data inconsistency
      dataSet.put(key, "IN_PROGRESS");
      dataSet.remove(key);

      // Transactional update. Use this code instead of the non-transactional updates above
      // to prevent data inconsistency
      // doInTransaction(context.getCache(), () -> dataSet.put(key, "IN_PROGRESS"));
      // doInTransaction(context.getCache(), () -> dataSet.remove(key);
    }

    rfc.getResultSender().lastResult(toReturn);
  }

  @Override
  public String getId() {
    return ID;
  }

}
