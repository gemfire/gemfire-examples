// Copyright 2026 Broadcom. All Rights Reserved.

package javaobject;

import java.util.*;
import java.io.*;
import org.apache.geode.*; // for DataSerializable
import org.apache.geode.cache.Declarable;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionAdapter;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;

public class InstantiateDSUser extends FunctionAdapter implements Declarable {

  public void execute(FunctionContext context) {

    Instantiator.register(new Instantiator(javaobject.User.class, 500) {
      public DataSerializable newInstance() {
        return new javaobject.User();
      }
    });

    ResultSender sender = context.getResultSender();
    sender.lastResult(0);
  }

  public String getId() {
    return "InstantiateDSUser";
  }

  public void init(Properties arg0) {
  }

}
