package com.example.demo.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZookeeperConf {
    @Value("${zk.url}")
    private String zkUrl;

    @Value("${zk.timeout}")
    private int timeout;

    @Bean
    public CuratorFramework getCuratorFramework(){
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3, 5000);

        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString(zkUrl)
                .sessionTimeoutMs(5000*4)
                .connectionTimeoutMs(5000*4)
                .retryPolicy(retryPolicy)
                .build();

        zkClient.start();

        return zkClient;
    }
}
