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

import static com.vmware.gemfire.examples.transactionsMixedWithNon.TransactionUtil.doInTransaction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;

public class Example {
  private static final long TEST_DURATION_SECONDS = 60;
  private static final int BATCH_SIZE = 100;

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    // connect to the locator using default port 10334
    ClientCache clientCache = new ClientCacheFactory().addPoolLocator("127.0.0.1", 10334)
        .set("log-level", "WARN").create();

    Region<String, String> region = clientCache
        .<String, String>createClientRegionFactory(ClientRegionShortcut.PROXY)
        .create("example-region");

    long start = System.nanoTime();
    int count = 0;
    while (System.nanoTime() - start < TimeUnit.SECONDS.toNanos(TEST_DURATION_SECONDS)) {
      CompletableFuture<Void> asyncUpdate =
          CompletableFuture.runAsync(() -> updateWithFunction(region));
      CompletableFuture<Void> putAll =
          CompletableFuture.runAsync(() -> doPutAllInTransaction(clientCache, region));
      putAll.get();
      asyncUpdate.get();

      verifyBucketCopies(clientCache);
      if (++count % 100 == 0) {
        System.out.print(".");
      }
    }

  }

  private static void updateWithFunction(Region<String, String> region) {
    ResultCollector<HashSet<String>, List<HashSet<String>>> collector =
        FunctionService.onRegion(region).execute(FindAndUpdateEntriesFunction.ID);

    List<HashSet<String>> results = collector.getResult();
  }

  private static void doPutAllInTransaction(ClientCache clientCache,
      Region<String, String> region) {
    Map<String, String> putAllBatch = new HashMap<>();
    ThreadLocalRandom random = ThreadLocalRandom.current();
    int bucket = random.nextInt();
    for (int j = 0; j < BATCH_SIZE; j++) {
      putAllBatch.put(bucket + "|" + random.nextInt(), "UNPROCESSED");
    }
    doInTransaction(clientCache, () -> region.putAll(putAllBatch));
  }

  private static void verifyBucketCopies(ClientCache cache) {
    ResultCollector<String, List<String>> collector =
        FunctionService.onServer(cache.getDefaultPool())
            .withArgs(new String[] {"example-region"})
            .execute(VerifyBucketCopiesFunction.ID);
    List<String> results = collector.getResult();

    if (!results.stream().allMatch("done"::equals)) {
      throw new RuntimeException("unexpected results " + results);
    }
  }
}
