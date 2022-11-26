package com.hdhxby.ecommerce;

import com.hdhxby.ecommerce.barriers.DistributedBarrierTest;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = ZookeeperApplication.class)
public class CuratorTest {


    private static final Logger log = LoggerFactory.getLogger(CuratorTest.class);

    String zookeeperConnectionString = "localhost:2181,localhost:2182,localhost:2183";

    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);

    @Before
    public void setup(){
        client.start();

    }

    @Test
    public void testData() throws Exception {
        if(client.checkExists().forPath("/hello") != null) {
            client.delete().deletingChildrenIfNeeded().forPath("/hello");
        }
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath("/hello","world".getBytes());
        client.getData().forPath("/hello");
        client.getData().usingWatcher(new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    log.debug("事件: {}", new String(client.getData().forPath(event.getPath())));
                } catch (Exception e) {
                    log.error("Exception",e);
                }
            }
        }).forPath("/hello");
//        client.getData().usingWatcher(new CuratorWatcher() {
//            @Override
//            public void process(WatchedEvent event) throws Exception {
//                log.debug("Curator事件: {}", new String(client.getData().forPath(event.getPath())));
//                event.getWrapper().getState();
//            }
//        }).forPath("/hello");
        CuratorFramework second = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        second.start();
        second.setData().forPath("/hello","world1".getBytes());
        second.setData().forPath("/hello","world2".getBytes());
        second.delete().forPath("/hello");
        second.close();
    }


    @Test
    public void testNodeCache() throws Exception {
        if(client.checkExists().forPath("/hello") != null) {
            client.delete().deletingChildrenIfNeeded().forPath("/hello");
        }
        client.create().forPath("/hello","world".getBytes());

        NodeCache nodeCache = new NodeCache(client,"/hello");
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                log.debug("NodeCache监听: {}", new String(client.getData().forPath("/hello")));
            }

        });
        nodeCache.start();

        CuratorFramework second = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        second.start();
        second.setData().forPath("/hello","world1".getBytes());
        second.setData().forPath("/hello","world2".getBytes());
        second.delete().forPath("/hello");
        second.close();
        nodeCache.close();
    }

    @Test
    public void testChildren() throws Exception {
        if(client.checkExists().forPath("/hello") != null) {
            client.delete().deletingChildrenIfNeeded().forPath("/hello");
        }
        client.create().forPath("/hello");
        client.getData().forPath("/hello");
        client.getChildren().usingWatcher(new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                log.debug("Watcher {}", event);
            }
        }).forPath("/hello");
//        client.getChildren().usingWatcher(new CuratorWatcher() {
//            @Override
//            public void process(WatchedEvent event) throws Exception {
//                log.debug("CuratorWatcher {}", event);
//                event.getWrapper().getState();
//            }
//        }).forPath("/hello");
        CuratorFramework second = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        second.start();
        second.create().forPath("/hello/world1","world1".getBytes());
        second.create().forPath("/hello/world2","world2".getBytes());
        client.delete().deletingChildrenIfNeeded().forPath("/hello");
        second.close();
    }

    @Test
    public void testPathChildrenCache() throws Exception {
        if(client.checkExists().forPath("/hello") != null) {
            client.delete().deletingChildrenIfNeeded().forPath("/hello");
        }
        client.create().forPath("/hello","world".getBytes());

        PathChildrenCache pathChildrenCache = new PathChildrenCache(client,"/hello",true);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                log.debug("PathChildrenCache监听: {}", new String(client.getData().forPath(event.getData().getPath())));
            }
        });
        pathChildrenCache.start();
        CuratorFramework second = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        second.start();
        second.setData().forPath("/hello","world1".getBytes());
        second.setData().forPath("/hello","world2".getBytes());
        second.delete().forPath("/hello");
        second.close();
        pathChildrenCache.close();
    }

    @After
    public void teardown(){
        client.close();
    }
}
