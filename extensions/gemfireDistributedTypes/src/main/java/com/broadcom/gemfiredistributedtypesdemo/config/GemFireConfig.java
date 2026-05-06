package com.broadcom.gemfiredistributedtypesdemo.config;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GemFireConfig {
    @Bean
    public ClientCache gemfireClientCache() {
        return new ClientCacheFactory()
                .addPoolLocator("localhost", 10334)
                .setPoolSubscriptionEnabled(true) 
                .create();
    }
}