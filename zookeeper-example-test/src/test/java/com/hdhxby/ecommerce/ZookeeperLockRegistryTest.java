package com.hdhxby.ecommerce;

import org.apache.zookeeper.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.zookeeper.lock.ZookeeperLockRegistry;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZookeeperApplication.class)
public class ZookeeperLockRegistryTest {

    private static final Logger log = LoggerFactory.getLogger(ZookeeperLockRegistryTest.class);

    @Autowired
    private ZookeeperLockRegistry zookeeperLockRegistry;

    @Test
    public void lock() throws IOException, InterruptedException, KeeperException {
        Assert.assertNotNull(zookeeperLockRegistry);
    }
}
