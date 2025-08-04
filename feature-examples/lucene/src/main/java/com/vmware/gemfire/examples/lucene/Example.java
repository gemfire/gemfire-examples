// Copyright (c) VMware, Inc. 2023. All rights reserved.

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
package com.vmware.gemfire.examples.lucene;

import java.util.ArrayList;
import java.util.Map;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneService;
import org.apache.geode.cache.lucene.LuceneServiceProvider;

public class Example {
  // These index names are predefined in gfsh scripts
  static final String SIMPLE_INDEX = "simpleIndex";
  static final String ANALYZER_INDEX = "analyzerIndex";
  static final String NESTEDOBJECT_INDEX = "nestedObjectIndex";

  // These region names are prefined in gfsh scripts
  static final String EXAMPLE_REGION = "example-region";

  public static void main(String[] args) throws LuceneQueryException {
    // connect to the locator using default port 10334
    ClientCache cache = new ClientCacheFactory().addPoolLocator("127.0.0.1", 10334)
        .set("log-level", "WARN").create();

    // create a local region that matches the server region
    Region<Integer, EmployeeData> region =
        cache.<Integer, EmployeeData>createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY)
            .create("example-region");

    insertValues(region);
    query(cache);
    queryNestedObject(cache);
    queryViaFunction(region);
    cache.close();
  }

  private static void queryViaFunction(Region<Integer, EmployeeData> region) {
    System.out.println("\nClient calls a function at server to query using " + SIMPLE_INDEX);
    String parameters = SIMPLE_INDEX + "," + EXAMPLE_REGION + ",JIVE,lastName,-1,false";
    Execution execution = FunctionService.onRegion(region).setArguments(parameters);
    ResultCollector<?, ?> rc = execution.execute("LuceneSearchIndexFunction");
    displayResults(rc);

    System.out.println("\nClient calls a function at server to query using " + NESTEDOBJECT_INDEX);
    LuceneQueryInfo queryInfo = new LuceneQueryInfo(NESTEDOBJECT_INDEX, EXAMPLE_REGION,
        "5035330001 AND 5036430001", "contacts.phoneNumbers", -1, false);
    execution = FunctionService.onRegion(region).setArguments(queryInfo);
    rc = execution.execute("LuceneSearchIndexFunction");
    displayResults(rc);
  }

  private static void displayResults(ResultCollector<?, ?> rc) {
    ArrayList functionResults = (ArrayList) ((ArrayList) rc.getResult()).get(0);

    System.out.println("\nClient Function found " + functionResults.size() + " results");
    functionResults.stream().forEach(result -> {
      System.out.println(result);
    });
  }

  private static void query(ClientCache cache) throws LuceneQueryException {
    LuceneService lucene = LuceneServiceProvider.get(cache);
    LuceneQuery<Integer, EmployeeData> query = lucene.createLuceneQueryFactory()
        .create(SIMPLE_INDEX, EXAMPLE_REGION, "firstName:Chris~2", "firstname");
    System.out.println("Employees with first names like Chris: " + query.findValues());
    LuceneQuery<Integer, EmployeeData> query2 =
        lucene.createLuceneQueryFactory().create(ANALYZER_INDEX, EXAMPLE_REGION,
            "lastName:hall~ AND email:Kris.Call\\@example.com", "lastName");
    System.out.println(
        "Compound search on last name and email using analyzerIndex: " + query.findValues());
  }

  private static void queryNestedObject(ClientCache cache) throws LuceneQueryException {
    LuceneService lucene = LuceneServiceProvider.get(cache);
    LuceneQuery<Integer, EmployeeData> query = lucene.createLuceneQueryFactory().create(
        NESTEDOBJECT_INDEX, EXAMPLE_REGION, "5035330001 AND 5036430001", "contacts.phoneNumbers");
    System.out.println("Employees with phone number 5035330001 and 5036430001 in their contacts: "
        + query.findValues());
  }

  public static void insertValues(Map<Integer, EmployeeData> region) {
    // insert values into the region
    String[] firstNames = "Alex,Bertie,Kris,Dale,Frankie,Jamie,Morgan,Pat,Ricky,Taylor".split(",");
    String[] lastNames = "Able,Bell,Call,Driver,Forth,Jive,Minnow,Puts,Reliable,Tack".split(",");
    String[] contactNames = "Jack,John,Tom,William,Nick,Jason,Daniel,Sue,Mary,Mark".split(",");
    int salaries[] = new int[] {60000, 80000, 75000, 90000, 100000};
    int hours[] = new int[] {40, 40, 40, 30, 20};
    int emplNumber = 10000;
    for (int index = 0; index < firstNames.length; index++) {
      emplNumber = emplNumber + index;
      Integer key = emplNumber;
      String email = firstNames[index] + "." + lastNames[index] + "@example.com";
      // Generating random number between 0 and 100000 for salary
      int salary = salaries[index % 5];
      int hoursPerWeek = hours[index % 5];

      ArrayList<Contact> contacts = new ArrayList();
      Contact contact1 = new Contact(contactNames[index] + " Jr",
          new String[] {"50353" + (30000 + index), "50363" + (30000 + index)});
      Contact contact2 = new Contact(contactNames[index],
          new String[] {"50354" + (30000 + index), "50364" + (30000 + index)});
      contacts.add(contact1);
      contacts.add(contact2);
      EmployeeData val = new EmployeeData(firstNames[index], lastNames[index], emplNumber, email,
          salary, hoursPerWeek, contacts);
      region.put(key, val);
    }
  }
}
