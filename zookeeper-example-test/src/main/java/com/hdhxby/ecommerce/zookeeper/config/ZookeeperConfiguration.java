package com.hdhxby.ecommerce.zookeeper.config;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.zookeeper.lock.ZookeeperLockRegistry;

@Configuration
public class ZookeeperConfiguration {

    private String registryKey = "test";

    @Bean
    @ConditionalOnMissingBean
    public ZookeeperLockRegistry redisLockRegistry(CuratorFramework client){
        return new ZookeeperLockRegistry(client,registryKey);
    }
}
