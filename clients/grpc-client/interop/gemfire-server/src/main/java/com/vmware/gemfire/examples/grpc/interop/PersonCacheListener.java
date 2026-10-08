// Copyright (c) 2026 Broadcom. All Rights Reserved.

package com.vmware.gemfire.examples.grpc.interop;

import com.example.test.v1.Person;
import com.google.protobuf.InvalidProtocolBufferException;

import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheListenerAdapter;

import com.vmware.gemfire.protobuf.ProtobufMessage;

/**
 * Verifies the server side of the interop: a value written over gRPC reaches an ordinary GemFire
 * callback, and unpacks there into the generated {@link Person} class.
 *
 * <p>
 * The stored value is a {@link ProtobufMessage}, so {@link ProtobufMessage#unpack} returns a
 * typed {@link Person} and the fields are read with the generated getters. The {@code grpc-worker}
 * thread in the logged line is the proof that a gRPC call drove a GemFire cache listener.
 *
 * <p>
 * Logging goes through the cache logger because {@code gfsh} does not capture a callback's
 * {@code System.out}.
 */
public class PersonCacheListener extends CacheListenerAdapter<String, ProtobufMessage> {

  @Override
  public void afterCreate(final EntryEvent<String, ProtobufMessage> event) {
    log("afterCreate", event);
  }

  @Override
  public void afterUpdate(final EntryEvent<String, ProtobufMessage> event) {
    log("afterUpdate", event);
  }

  private void log(final String operation, final EntryEvent<String, ProtobufMessage> event) {
    final ProtobufMessage message = event.getNewValue();

    final Person person;
    try {
      person = message.unpack(Person.class);
    } catch (final InvalidProtocolBufferException invalidProtocolBufferException) {
      throw new IllegalStateException("Value for key " + event.getKey() + " is not a Person",
          invalidProtocolBufferException);
    }

    event.getRegion().getCache().getLogger().info("[PersonCacheListener] " + operation
        + " key=" + event.getKey()
        + " originRemote=" + event.isOriginRemote()
        + " valueClass=" + message.getClass().getName()
        + " firstName=" + person.getFirstName()
        + " lastName=" + person.getLastName());
  }
}
