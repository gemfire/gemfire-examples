// Copyright (c) VMware, Inc. 2023.
// All rights reserved. SPDX-License-Identifier: Apache-2.0

package com.vmware.gemfire.examples.launchers;

import org.apache.geode.distributed.LocatorLauncher;

public class ExampleLocatorApplication {
  public static void main(final String[] args) {
    final LocatorLauncher locatorLauncher =
        new LocatorLauncher.Builder().setMemberName("locator1").setPort(13489).build();

    locatorLauncher.start();

    System.out.println("Locator successfully started");
    System.out.println(args[0]);
  }
}
