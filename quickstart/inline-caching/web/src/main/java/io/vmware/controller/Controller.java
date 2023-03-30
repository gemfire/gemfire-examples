/*
 * Copyright 2019 - 2021. VMware, Inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.vmware.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @ResponseBody
    @GetMapping("/{key}")
    public String getValue(@PathVariable String key) {
        System.out.println("key: "+ key);
        String value = service.getValue(key);

        return value;
    }

    @PutMapping("/{key}/{value}")
    public void putValue(@PathVariable String key, @PathVariable String value) {
        System.out.println("key: "+ key + " value: "+value);
        service.putValue(key, value);
    }
}
