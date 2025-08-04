// Copyright (c) VMware, Inc. 2023. All rights reserved.
package com.vmware.gemfire.examples.json;

import static org.apache.geode.cache.client.ClientRegionShortcut.PROXY;

import java.util.List;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.query.FunctionDomainException;
import org.apache.geode.cache.query.NameResolutionException;
import org.apache.geode.cache.query.QueryInvocationTargetException;
import org.apache.geode.cache.query.TypeMismatchException;
import org.apache.geode.json.JsonDocument;
import org.apache.geode.json.JsonDocumentFactory;
import org.apache.geode.json.JsonParseException;

public class Example {

  private static final int NUM_KEYS = 10;

  public static void main(String[] args) throws FunctionDomainException, TypeMismatchException,
      QueryInvocationTargetException, NameResolutionException, JsonParseException {
    // connect to the locator using default port 10334
    ClientCache cache = new ClientCacheFactory().addPoolLocator("127.0.0.1", 10334)
        .set("log-level", "WARN").create();

    // create a local region that matches the server region
    Region<Long, JsonDocument> region =
        cache.<Long, JsonDocument>createClientRegionFactory(PROXY).create("example-region");
    // The default is StorageFormat.BSON for cache.getJsonDocumentFactory()
    // If you prefer StorageFormat.PDX, use cache.getJsonDocumentFactory(StorageFormat.PDX)
    JsonDocumentFactory jsonDocumentFactory = cache.getJsonDocumentFactory();
    for (long i = 0; i < NUM_KEYS; i++) {
      JsonDocument jsonDocument = jsonDocumentFactory.create(createJson(i));
      region.put(i, jsonDocument);
    }

    JsonDocument jsonDocument = region.get(0L);

    System.out.println("The JSON string is:\n\n" + jsonDocument.toJson());

    System.out.println("\nUsing JsonDocument.getField(), the value of JSON intField is: "
        + jsonDocument.getField("intField"));

    JsonDocument nestedField = (JsonDocument) jsonDocument.getField("nestedField");
    System.out.println(
        "\nUsing JsonDocument.getField(), the value of JSON nestedField is: " + nestedField);

    System.out.println("\nUsing JsonDocument.getField() to get field2 of nestedField: "
        + nestedField.getField("field2"));

    List arrayField = (List) jsonDocument.getField("arrayField");
    System.out.println("\nUsing JsonDocument.getField() to get arrayField: " + arrayField);

    System.out
        .println("\nUsing List.get(), the value of JSON arrayField[1] is: " + arrayField.get(1));

    System.out.println("\nQuery: select * from /example-region where name='name5'");
    System.out.println("\nQuery result:\n\n" + cache.getQueryService()
        .newQuery("select * from /example-region where name='name5'").execute());

    System.out.println("\nQuery: select * from /example-region where arrayField[0]=6");
    System.out.println("\nQuery result:\n\n" + cache.getQueryService()
        .newQuery("select * from /example-region where arrayField[0]=6").execute());

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
