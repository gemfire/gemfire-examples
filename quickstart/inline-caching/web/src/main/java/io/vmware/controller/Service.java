/*
 * Copyright 2019 - 2021. VMware, Inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.vmware.controller;

import java.util.Optional;

import org.springframework.data.gemfire.repository.Wrapper;

@org.springframework.stereotype.Service
public class Service {

    private Repository repository;

    public Service(Repository repository) {
        this.repository = repository;
    }

    public String getValue(String key) {
        Optional<String> value = repository.findById(key);
        return value.orElse("NULL");
    }

  public void putValue(String key, String value) {
    repository.save(new Wrapper<>(value, key));
  }
}
