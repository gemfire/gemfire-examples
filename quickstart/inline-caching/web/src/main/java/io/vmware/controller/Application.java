/*
 * Copyright 2019 - 2021. VMware, Inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.vmware.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableClusterDefinedRegions;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;

@SpringBootApplication
@EnableClusterDefinedRegions
@ClientCacheApplication
@EnableGemfireRepositories(basePackageClasses = Repository.class)
//@EnableClusterAware
//@CacheServerApplication(locators = "127.0.0.1[10334]")
public class Application {


	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
