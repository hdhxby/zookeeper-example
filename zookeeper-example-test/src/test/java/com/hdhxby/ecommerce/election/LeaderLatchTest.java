package com.hdhxby.ecommerce.election;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.checkerframework.checker.units.qual.C;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/***
 * Leader屏障
 */
public class LeaderLatchTest {

    private static final Logger log = LoggerFactory.getLogger(LeaderLatchTest.class);

    private static final int COUNT = 1;

    String zookeeperConnectionString = "localhost:2181,localhost:2182,localhost:2183";

    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    CountDownLatch countDownLatch = new CountDownLatch(COUNT);

    @Before
    public void setup(){
        client.start();
    }

    @Test
    public void test() throws Exception {
        List<Future<CuratorFramework>> futures = new ArrayList<>();
        for (int i=0;i<COUNT;i++) {
            futures.add(executorService.submit(() -> {
                    CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
                    client.start();
                    try (LeaderLatch latch = new LeaderLatch(client, "/think/recipes/leader/latch")) {
                        latch.start(); // 启动
                        latch.await();
                        countDownLatch.countDown();
                        Thread.sleep(1000l);
                        log.info("{} hasLeadership {}", Thread.currentThread().getName(), latch.hasLeadership());
                        Thread.sleep(1000l);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        return client;
                    }
            }));
        }
        countDownLatch.await();
        try(LeaderLatch leaderLatch = new LeaderLatch(client,"/think/recipes/leader/leaderLatch")) {
            leaderLatch.start(); // 启动
            log.info("{} hasLeadership {}", Thread.currentThread().getName(), leaderLatch.hasLeadership());
            leaderLatch.await(); // 等待成为Leader
            log.info("{} hasLeadership {}", Thread.currentThread().getName(), leaderLatch.hasLeadership());
        }
        for (Future<CuratorFramework> future : futures) {
            future.get().close();
        }
    }

    @After
    public void teardown(){
        client.close();
    }
}
