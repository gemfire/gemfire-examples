// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Claude
 * Description:
 * 2026-09-09: Server-side listener that reads Protobuf values written over the gRPC API.
 */

package com.vmware.gemfire.examples.grpc.interop;

import org.apache.geode.cache.Document;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheListenerAdapter;

/**
 * Verifies the server side of the interop: a value written over gRPC reaches an ordinary GemFire
 * callback, and its Protobuf fields are readable there by name.
 *
 * <p>
 * The stored value implements GemFire's {@link Document}, so {@link Document#getField} reads it
 * without this class naming any extension type — necessary, because a deployed jar loads in an
 * isolated module that cannot resolve them. Field names are the Protobuf ones, so
 * {@code first_name}. The {@code grpc-worker} thread in the logged line is the proof that a gRPC
 * call drove a GemFire cache listener.
 *
 * <p>
 * Logging goes through the cache logger because {@code gfsh} does not capture a callback's
 * {@code System.out}.
 */
public class PersonCacheListener extends CacheListenerAdapter<String, Object> {

  @Override
  public void afterCreate(final EntryEvent<String, Object> event) {
    log("afterCreate", event);
  }

  @Override
  public void afterUpdate(final EntryEvent<String, Object> event) {
    log("afterUpdate", event);
  }

  private void log(final String operation, final EntryEvent<String, Object> event) {
    final Object newValue = event.getNewValue();
    final Document fields = (Document) newValue;
    event.getRegion().getCache().getLogger().info("[PersonCacheListener] " + operation
        + " key=" + event.getKey()
        + " originRemote=" + event.isOriginRemote()
        + " valueClass=" + newValue.getClass().getName()
        + " first_name=" + fields.getField("first_name")
        + " last_name=" + fields.getField("last_name"));
  }
}
