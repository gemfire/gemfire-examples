// Copyright (c) VMware, Inc. 2023.
// All rights reserved. SPDX-License-Identifier: Apache-2.0

package com.vmware.gemfire.examples.launchers;

import org.apache.geode.distributed.ServerLauncher;

public class ExampleServerApplication {
  public static void main(final String[] args) {
    final ServerLauncher serverLauncher = new ServerLauncher.Builder().setMemberName("server1")
        .setServerPort(40405).set("jmx-manager", "true").set("jmx-manager-start", "true")
        .set("log-file", "").build();

    serverLauncher.start();

    System.out.println("Cache server successfully started");
    System.out.println(args[0]);
  }
}
