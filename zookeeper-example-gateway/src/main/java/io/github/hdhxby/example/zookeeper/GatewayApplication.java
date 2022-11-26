package io.github.hdhxby.example.zookeeper;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关(gateway)
 * @author lixiaobin
 * @version 2.0.0
 * @since 2.0.0
 */
@EnableDiscoveryClient
@SpringCloudApplication
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
