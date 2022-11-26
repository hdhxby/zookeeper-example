package com.hdhxby.ecommerce.zookeeper;

import java.util.concurrent.locks.Lock;

public class LockTemplate{

    private Lock lock;

    public LockTemplate(Lock lock) {
        this.lock = lock;
    }

    public <R> R  execute(Execute<R> function){
        try {
            lock.lock();
            return function.apply();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

}
