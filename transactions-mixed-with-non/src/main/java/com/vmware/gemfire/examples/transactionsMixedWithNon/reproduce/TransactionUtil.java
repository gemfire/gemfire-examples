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

import org.apache.geode.cache.CommitConflictException;
import org.apache.geode.cache.GemFireCache;

public class TransactionUtil {

  /**
   * Execute a runnable in a transaction, retrying until it succeeds without conflict.
   */
  public static void doInTransaction(GemFireCache cache, Runnable r) {
    boolean done = false;
    while (!done) {
      try {
        cache.getCacheTransactionManager().begin();
        r.run();
        cache.getCacheTransactionManager().commit();
        done = true;
      } catch (CommitConflictException e) {
        // retry
      }
    }
  }
}
