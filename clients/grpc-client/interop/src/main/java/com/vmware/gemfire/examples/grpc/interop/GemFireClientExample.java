// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Claude
 * Description:
 * 2026-09-09: GemFire client that reads gRPC-written Protobuf and writes a message of its own.
 * 2026-09-16: Uses ProtobufMessage, which replaced ProtoAnyDocument in the extension.
 */

package com.vmware.gemfire.examples.grpc.interop;

import com.example.test.v1.Person;
import com.google.protobuf.InvalidProtocolBufferException;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

import com.vmware.gemfire.proto.ProtobufMessage;

/**
 * Uses the GemFire client API on the same entries the gRPC API writes, in both directions: it
 * reads the messages the Go client wrote with {@code --mode=put}, then writes one that the same
 * client reads back over gRPC with {@code --mode=get}.
 *
 * <p>
 * A {@code get} returns a {@link ProtobufMessage} because {@code gemfire-proto-serialization}
 * is on this application's classpath. That jar registers the decoder for GemFire's PROTOBUF
 * DSCODE. Without it the client cannot deserialize the value at all.
 *
 * <p>
 * Only the server holds a proto type registry, so {@code getField} is not available here.
 * {@code unpack} is, because it uses the generated class instead of the registry.
 */
public class GemFireClientExample {

  private static final String REGION_NAME = "people";

  public static void main(final String[] args) {
    // The locator scripts/start.gfsh starts, on GemFire's default port.
    final var clientCache = new ClientCacheFactory()
        .addPoolLocator("127.0.0.1", 10334)
        .set("log-level", "error")
        .create();
    try {
      final var region = clientCache
          .<String, ProtobufMessage>createClientRegionFactory(ClientRegionShortcut.PROXY)
          .create(REGION_NAME);

      System.out.println("=== Reading what the gRPC client wrote ===");
      read(region, "alice");
      read(region, "bob");

      System.out.println("=== Writing a message for the gRPC client to read ===");
      write(region, "grace", Person.newBuilder()
          .setFirstName("Grace").setLastName("Hopper").build());
    } finally {
      clientCache.close();
    }
  }

  private static void read(final Region<String, ProtobufMessage> region, final String key) {
    final ProtobufMessage message = region.get(key);
    if (message == null) {
      System.out.println("GemFire get " + key + " -> not found");
      return;
    }

    System.out.println("GemFire get " + key + " -> " + message.getClass().getName());
    try {
      final Person person = message.unpack(Person.class);
      System.out.println("  Person: " + person.getFirstName() + " " + person.getLastName());
    } catch (final InvalidProtocolBufferException invalidProtocolBufferException) {
      System.out.println("  unpack failed: " + invalidProtocolBufferException);
    }
  }

  private static void write(final Region<String, ProtobufMessage> region, final String key,
      final Person person) {
    region.put(key, ProtobufMessage.pack(person));
    System.out.println("GemFire put " + key + " -> " + person.getFirstName() + " "
        + person.getLastName());
  }
}
