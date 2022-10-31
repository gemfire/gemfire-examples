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
package org.apache.geode_examples.json;

import java.util.List;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.query.FunctionDomainException;
import org.apache.geode.cache.query.NameResolutionException;
import org.apache.geode.cache.query.QueryInvocationTargetException;
import org.apache.geode.cache.query.TypeMismatchException;
import org.apache.geode.json.JsonDocument;
import org.apache.geode.json.JsonParseException;

public class Example {

  private static final int NUM_KEYS = 10000;

  public static void main(String[] args) {
    // connect to the locator using default port 10334
    ClientCache cache = new ClientCacheFactory().addPoolLocator("127.0.0.1", 10334)
        .set("log-level", "WARN").setPdxReadSerialized(true).create();

    // create a local region that matches the server region
    Region<Long, JsonDocument> region =
        cache.<Long, JsonDocument>createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY)
            .create("example-region");

    for (long i = 0; i < NUM_KEYS; i++) {
      try {
        JsonDocument jsonDocument = cache.getJsonDocumentFactory().create(createJson(i));
        region.put(i, jsonDocument);
      } catch (JsonParseException e) {
        throw new RuntimeException(e);
      }
    }

    JsonDocument jsonDocument = region.get(0L);
    System.out.println("The JSON string is:\n\n" + jsonDocument.toJson());
    System.out.println("\nUsing Doucment.getField(), the value of JSON intField is "
        + jsonDocument.getField("intField"));
    System.out.println("\nUsing Doucment.getField(), the value of JSON nestedField.field2 is "
        + ((JsonDocument) jsonDocument.getField("nestedField")).getField("field2"));
    System.out.println("\nUsing Doucment.getField(), the value of JSON arrayField[1] is "
        + ((List) jsonDocument.getField("arrayField")).get(1));

    try {
      System.out.println("\nQuery: select * from /example-region where name='name5'");
      System.out.println("\nQuery result:\n\n" + region.getRegionService().getQueryService()
          .newQuery("select * from /example-region where name='name5'").execute());
      System.out.println("\nQuery: select * from /example-region where arrayField[0]=5");
      System.out.println("\nQuery result:\n\n" + region.getRegionService().getQueryService()
          .newQuery("select * from /example-region where arrayField[0]=5").execute());
    } catch (FunctionDomainException | TypeMismatchException | NameResolutionException
        | QueryInvocationTargetException e) {
      throw new RuntimeException(e);
    }
    cache.close();
  }

  private static String createJson(long i) {
    StringBuilder result = new StringBuilder();
    result.append("{");
    result.append("\"booleanField\":").append("true");
    result.append(",\"intField\":").append(i);
    result.append(",\"name\":").append("\"name").append(i).append("\"");
    result.append(",\"nestedField\":")
        .append("{\"field1\":5, \"field2\":\"something\", \"field3\":55555.0}");
    result.append(",\"arrayField\":").append("[").append(i).append(", \"Fred\", 4.0, true]");
    result.append("}");
    return result.toString();
  }
}
