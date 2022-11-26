package com.hdhxby.ecommerce.zookeeper;

@FunctionalInterface
public interface Execute<R> {
    R apply();
}
