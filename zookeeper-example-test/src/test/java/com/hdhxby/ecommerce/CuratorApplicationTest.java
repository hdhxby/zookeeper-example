package com.hdhxby.ecommerce;

import org.apache.curator.framework.CuratorFramework;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZookeeperApplication.class)
public class CuratorApplicationTest {

    private static final Logger log = LoggerFactory.getLogger(CuratorApplicationTest.class);

    @Autowired
    private CuratorFramework client;

    /**
     * 增删改查
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        Assert.assertNotNull(client);
    }

}
