package com.hdhxby.ecommerce.locks;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 进程间互斥
 */
public class InterProcessMutexTest {

    private static final Logger log = LoggerFactory.getLogger(InterProcessMutexTest.class);

    private static final int COUNT = 1;

    String zookeeperConnectionString = "localhost:2181,localhost:2182,localhost:2183";

    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);


    ExecutorService executorService = Executors.newSingleThreadExecutor();

    CountDownLatch countDownLatch = new CountDownLatch(COUNT);

    @Before
    public void setup() {
        client.start();
    }

    @Test
    public void test() throws Exception {
        List<Future<CuratorFramework>> futures = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            futures.add(executorService.submit(() -> {
                CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
                try {
                    client.start();
                    InterProcessMutex lock = new InterProcessMutex(client, "/think/locks");
                    lock.acquire();
                    log.info("{} 获取锁 {}", Thread.currentThread().getName(), lock.isOwnedByCurrentThread());
                    countDownLatch.countDown();
                    Thread.sleep(5000l);
                    log.info("{} 释放锁 {}", Thread.currentThread().getName(), lock.isOwnedByCurrentThread());
                    lock.release();
                } finally {
                    return client;
                }
            }));
        }
        InterProcessMutex lock = new InterProcessMutex(client, "/think/locks");
        log.info("{} 等待锁 {}", Thread.currentThread().getName(), lock.isOwnedByCurrentThread());
        countDownLatch.await();
        lock.acquire();
        log.info("{} 获取锁 {}", Thread.currentThread().getName(), lock.isOwnedByCurrentThread());
        log.info("{} 释放锁 {}", Thread.currentThread().getName(), lock.isOwnedByCurrentThread());
        lock.release();
        for (Future<CuratorFramework> future : futures) {
            future.get().close();
        }
    }

    @After
    public void teardown() {
        client.close();
    }
}
