// Copyright (c) VMware, Inc. 2022.
// All rights reserved. SPDX-License-Identifier: Apache-2.0

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
package org.apache.geode_examples.wanDelta;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;

import org.apache.geode.DataSerializable;
import org.apache.geode.DataSerializer;
import org.apache.geode.Delta;
import org.apache.geode.InvalidDeltaException;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;

public class SimpleDelta implements DataSerializable, Delta {

  private int id;

  private int version;

  private byte[] payload;

  private boolean hasDelta;

  public SimpleDelta() {}

  public SimpleDelta(int id, byte[] payload) {
    this.id = id;
    this.payload = payload;
    this.version = 0;
  }

  public void update(Region region) {
    hasDelta = true;
    version++;
    region.put(id, this);
    hasDelta = false;
  }

  public boolean hasDelta() {
    return hasDelta;
  }

  public void toDelta(DataOutput out) throws IOException {
    out.writeInt(version);
  }

  public void fromDelta(DataInput in) throws IOException, InvalidDeltaException {
    version = in.readInt();
    CacheFactory.getAnyInstance().getLogger().info("SimpleDelta.fromDelta invoked on " + this);
  }

  public void toData(DataOutput out) throws IOException {
    out.writeInt(id);
    out.writeInt(version);
    DataSerializer.writeByteArray(payload, out);
  }

  public void fromData(DataInput in) throws IOException, ClassNotFoundException {
    id = in.readInt();
    version = in.readInt();
    payload = DataSerializer.readByteArray(in);
    CacheFactory.getAnyInstance().getLogger().info("SimpleDelta.fromData invoked on " + this);
  }

  public String toString() {
    return new StringBuilder().append(getClass().getSimpleName()).append("[").append("id=")
        .append(id).append("; version=").append(version).append("; payloadLength=")
        .append(payload.length).append("]").toString();
  }
}
