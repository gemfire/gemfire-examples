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
import java.util.List;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneResultStruct;
import org.apache.geode.cache.lucene.LuceneService;
import org.apache.geode.cache.lucene.LuceneServiceProvider;
import org.apache.geode.cache.lucene.PageableLuceneQueryResults;

/**
 * The LuceneSearchIndexFunction class is a function used to collect the information on a particular
 * lucene index.
 * </p>
 *
 * @see Cache
 * @see org.apache.geode.cache.execute.Function
 * @see FunctionAdapter
 * @see FunctionContext
 * @see InternalEntity
 * @see LuceneIndexDetails
 * @see LuceneIndexInfo
 */
@SuppressWarnings("unused")
public class LuceneSearchIndexFunction<K, V> implements Function {

  protected Cache getCache() {
    return CacheFactory.getAnyInstance();
  }

  public String getId() {
    return LuceneSearchIndexFunction.class.getSimpleName();
  }

  private LuceneQueryInfo createQueryInfoFromString(String strParm) {
    String params[] = strParm.split(",");
    // "personIndex,Person,name:Tom99*,name,-1,false"
    int limit = Integer.parseInt((String) params[4]);
    boolean isKeyOnly = Boolean.parseBoolean((String) params[5]);
    LuceneQueryInfo queryInfo = new LuceneQueryInfo((String) params[0] /* index name */,
        (String) params[1] /* regionPath */, (String) params[2] /* queryString */,
        (String) params[3] /* default field */, limit, isKeyOnly);
    return queryInfo;
  }

  private LuceneQueryInfo createQueryInfoFromString(Object[] params) {
    // "personIndex,Person,name:Tom99*,name,-1,false"
    int limit = (int) params[4];
    boolean isKeyOnly = (boolean) params[5];
    LuceneQueryInfo queryInfo = new LuceneQueryInfo((String) params[0] /* index name */,
        (String) params[1] /* regionPath */, (String) params[2] /* queryString */,
        (String) params[3] /* default field */, limit, isKeyOnly);
    return queryInfo;
  }

  public void execute(final FunctionContext context) {
    final Cache cache = getCache();
    LuceneQueryInfo queryInfo = null;
    Object args = context.getArguments();
    if (args instanceof LuceneQueryInfo) {
      queryInfo = (LuceneQueryInfo) args;
    } else if (args instanceof String) {
      String strParm = (String) args;
      queryInfo = createQueryInfoFromString(strParm);
    } else if (args instanceof Object[]) {
      queryInfo = createQueryInfoFromString((Object[]) args);
    }

    LuceneService luceneService = LuceneServiceProvider.get(getCache());
    try {
      if (luceneService.getIndex(queryInfo.getIndexName(), queryInfo.getRegionPath()) == null) {
        throw new Exception("Index " + queryInfo.getIndexName() + " not found on region "
            + queryInfo.getRegionPath());
      }
      final LuceneQuery<K, V> query = luceneService.createLuceneQueryFactory()
          .setLimit(queryInfo.getLimit()).create(queryInfo.getIndexName(),
              queryInfo.getRegionPath(), queryInfo.getQueryString(), queryInfo.getDefaultField());
      if (queryInfo.getKeysOnly()) {
        context.getResultSender().lastResult(query.findKeys());
      } else {
        PageableLuceneQueryResults<K, V> pageableLuceneQueryResults = query.findPages();
        List<LuceneResultStruct<K, V>> pageResult = new ArrayList();
        while (pageableLuceneQueryResults.hasNext()) {
          List<LuceneResultStruct<K, V>> page = pageableLuceneQueryResults.next();
          pageResult.addAll(page);
        }
        context.getResultSender().lastResult(pageResult);
      }
    } catch (LuceneQueryException e) {
      context.getResultSender()
          .lastResult(new LuceneSearchResults(true, e.getRootCause().getMessage()));
    } catch (Exception e) {
      context.getResultSender().lastResult(new LuceneSearchResults(true, e.getMessage()));
    }
  }
}
