/*
 * Copyright 2024 Broadcom. All rights reserved.
 */

package com.vmware.gemfire.examples.launchers;

import org.apache.geode.distributed.LocatorLauncher;

public class CustomLocatorApplication {
  public static void main(final String[] args) {
    final LocatorLauncher locatorLauncher = new LocatorLauncher.Builder()
        .setMemberName(args[0])
        .setPort(Integer.parseInt(args[1]))
        .set("log-file", "")
        .build();

    locatorLauncher.start();

    System.out.println("Locator successfully started");
  }
}
