package com.hdhxby.ecommerce.barriers;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class DistributedBarrierTest {

    private static final Logger log = LoggerFactory.getLogger(DistributedBarrierTest.class);

    String zookeeperConnectionString = "localhost:2181,localhost:2182,localhost:2183";

    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);

    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Before
    public void setup(){
        client.start();
    }

    @Test
    public void test() throws Exception {
        executorService.schedule(()->{
            try {
//                Thread.sleep(1000l); // 等待1s
                DistributedBarrier distributedBarrier = new DistributedBarrier(client,"/think/recipes/barriers");
                distributedBarrier.setBarrier(); // 设置屏障
                log.info("设置屏障");
            } catch (Exception e) {
                e.printStackTrace();
            }
        },1l, TimeUnit.SECONDS);
        log.info("等待屏障");
        DistributedBarrier distributedBarrier = new DistributedBarrier(client,"/think/recipes/barriers");
        distributedBarrier.waitOnBarrier(); // 等待屏障
        log.info("移除屏障");
        distributedBarrier.removeBarrier(); // 移除屏障
    }

    @After
    public void teardown(){
        client.close();
    }
}
