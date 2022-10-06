/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

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
package org.apache.geode_examples.loader;

import static org.apache.geode_examples.loader.Example.printQuotes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.mockito.Mockito;

import org.apache.geode.cache.LoaderHelper;

public class ExampleTest {

  @Rule
  public SystemOutRule systemOutRule = new SystemOutRule().enableLog();

  @Test
  public void testExample() throws Exception {
    QuoteLoader loader = new QuoteLoader();
    Map<String, String> region = Mockito.spy(new HashMap<>());

    when(region.get(any())).then(inv -> {
      String key = inv.getArgument(0);
      LoaderHelper<String, String> helper = mock(LoaderHelper.class);
      when(helper.getKey()).thenReturn(key);

      return loader.load(helper);
    });

    printQuotes(region);

    assertThat(systemOutRule.getLog()).contains("Anton Chekhov");
    assertThat(systemOutRule.getLog()).contains("Loaded 20 definitions");
    assertThat(systemOutRule.getLog()).contains("Fetched 20 cached definitions");
  }
}
