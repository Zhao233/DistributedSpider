//package com.example.demo.zookeeper;
//
//import org.apache.curator.framework.CuratorFramework;
//import org.apache.curator.framework.recipes.cache.PathChildrenCache;
//import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
//import org.apache.curator.framework.recipes.locks.InterProcessLock;
//import org.apache.curator.framework.recipes.locks.InterProcessMutex;
//import org.apache.zookeeper.CreateMode;
//import org.apache.zookeeper.ZooDefs;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.CountDownLatch;
//
//@Component("zklock")
//public class ZKLock{
//    @Autowired
//    private CuratorFramework zkClient;
//
//    @Value("${zk.localPath}")
//    private String lockPath;
//
//    InterProcessLock lock = new InterProcessMutex(zkClient, lockPath);
//    CountDownLatch countDownLatch = new CountDownLatch(2);
//
//    @Autowired
//    private CuratorFramework curatorFramework;
//
//    public void tryLock() throws Exception {
//        lock.acquire();
//    }
//
//}
