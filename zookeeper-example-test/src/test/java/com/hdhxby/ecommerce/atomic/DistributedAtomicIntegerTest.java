package com.hdhxby.ecommerce.atomic;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class DistributedAtomicIntegerTest {

    private static final Logger log = LoggerFactory.getLogger(DistributedAtomicIntegerTest.class);

    private static final int COUNT = 10;

    String zookeeperConnectionString = "localhost:2181,localhost:2182,localhost:2183";

    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);

    ExecutorService executorService = Executors.newCachedThreadPool();


    @Before
    public void setup(){
        client.start();
    }

    @Test
    public void test() throws Exception {
        final int count = 10;
        CountDownLatch countDownLatch = new CountDownLatch(2);

        CyclicBarrier cyclicBarrier = new CyclicBarrier(COUNT,() -> {
            countDownLatch.countDown();
        });

        DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(client, "/think/recipes/atomic/distributedAtomicInteger", retryPolicy);
        atomicInteger.initialize(0);
        atomicInteger.forceSet(0);

        for(int i =0;i<COUNT;i++) {
            executorService.submit(() -> {
                try(CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy)) {
                    client.start();
                    cyclicBarrier.await();
                    log.info("{} 开始累加", Thread.currentThread());
                    DistributedAtomicInteger distributedAtomicInteger = new DistributedAtomicInteger(client, "/think/recipes/atomic/distributedAtomicInteger", retryPolicy);
                    for (int j =0;j< count;j++){
                        AtomicValue atomicValue;
                        do {
                            atomicValue = distributedAtomicInteger.increment();
                        } while (!atomicValue.succeeded());

                    }
                    log.info("{} increment {}", Thread.currentThread(), distributedAtomicInteger.get().preValue());
                    cyclicBarrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            });
        }
        countDownLatch.await();
        Assert.assertEquals(COUNT * count,atomicInteger.get().preValue().intValue());
    }

    @After
    public void teardown(){
        client.close();
    }
}
