package com.hdhxby.ecommerce.zookeeper;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.integration.zookeeper.lock.ZookeeperLockRegistry;

/**
 * 分布式锁的工厂
 *
 * @author lixiaobin
 * @version 2.0.0
 * @since .0.0
 */
public class LockFactory implements ApplicationContextAware, FactoryBean {

    private ApplicationContext applicationContext;

    public LockTemplate getLockTemplate(String name) throws BeansException {
        return new LockTemplate(applicationContext.getBean(ZookeeperLockRegistry.class)
            .obtain(name));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }
}
