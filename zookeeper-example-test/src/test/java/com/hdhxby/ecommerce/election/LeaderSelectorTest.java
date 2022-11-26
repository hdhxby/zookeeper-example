package com.hdhxby.ecommerce.election;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Leader选举
 */
public class LeaderSelectorTest {

    private static final Logger log = LoggerFactory.getLogger(LeaderSelectorTest.class);

    private static final int COUNT = 3;

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

        for(int i=0;i<COUNT;i++) {
            futures.add(executorService.submit(() -> {
                CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
                client.start();
                CountDownLatch latch = new CountDownLatch(1);
                try(LeaderSelector selector = new LeaderSelector(client, "/think/recipes/leader/selector", new LeaderSelectorListener() {
                    @Override
                    public void stateChanged(CuratorFramework client, ConnectionState newState) {
                        log.info("{} stateChanged: {}", Thread.currentThread(), newState);
                        if (ConnectionState.CONNECTED.equals(newState)) {
                            log.info("{} 连接: {}", Thread.currentThread(), newState);
                        }
                        if (ConnectionState.LOST.equals(newState)) {
                            log.info("{} 丢失: {}", Thread.currentThread(), newState);
                        }
                    }

                    @Override
                    public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                        try {
                            log.info("{} 获取Leadership: {}", Thread.currentThread(), curatorFramework);
                            Thread.sleep(1000l);
                            log.info("{} 释放Leadership: {}", Thread.currentThread(), curatorFramework);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            latch.countDown();
                        }
                    }
                })){
                    selector.start();
                    latch.await();
                    countDownLatch.countDown();
                }
                return client;
            }));
        }
        countDownLatch.await();
        for (Future<CuratorFramework> future : futures) {
            future.get().close();
        }
    }

    @After
    public void teardown(){
        client.close();
    }
}
