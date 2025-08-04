/*
 * Copyright 2024 Broadcom. All rights reserved.
 */

package com.vmware.gemfire.examples.launchers;

import org.apache.geode.distributed.ServerLauncher;

public class CustomServerApplication {
  public static void main(final String[] args) {
    final ServerLauncher serverLauncher = new ServerLauncher.Builder()
        .setMemberName(args[0])
        .setServerPort(Integer.parseInt(args[1]))
        .set("log-file", "")
        .build();

    serverLauncher.start();

    System.out.println("Server successfully started");
  }
}
