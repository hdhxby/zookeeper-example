package com.hdhxby.ecommerce;

import com.hdhxby.ecommerce.zookeeper.LockFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZookeeperApplication.class)
public class LockFactoryTest {

    private static final Logger log = LoggerFactory.getLogger(LockFactoryTest.class);

    @Autowired
    private LockFactory lockFactory;

    @Test
    public void lock(){
        lockFactory.getLockTemplate("/hello")
            .execute(() -> {
               return null;
            });
    }
}
