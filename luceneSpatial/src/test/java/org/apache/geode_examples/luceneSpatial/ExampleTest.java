// Copyright (c) VMware, Inc. 2022.
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
package org.apache.geode_examples.luceneSpatial;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.apache.geode.cache.lucene.LuceneService;

public class ExampleTest {

  @Test
  public void testPutEntries() throws InterruptedException {
    LuceneService service = mock(LuceneService.class);
    Map<String, LocationObject> region = new HashMap<String, LocationObject>();
    CommonOps.putEntries(region);
    service.waitUntilFlushed("simpleIndex", region.toString(), 1, TimeUnit.MINUTES);
    assertEquals(7, region.size());

  }
}
