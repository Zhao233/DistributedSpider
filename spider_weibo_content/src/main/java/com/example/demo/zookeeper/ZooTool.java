package com.example.demo.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ZooTool {
    final int page_step = 10;

    @Value("${zk.pagePath}")
    private String pagePath;

    @Value("${zk.lockPath}")
    private String lockPath;

    @Autowired
    private CuratorFramework zkClient;

    private InterProcessMutex lock = new InterProcessMutex(zkClient, lockPath);

    public int getPageFromRemote(){
        try {
            lock.acquire();

            String page_original = String.valueOf(zkClient.getData().forPath(pagePath));
            int page = Integer.parseInt(page_original);

            int page_res = page + page_step;

            zkClient.setData().forPath(pagePath, String.valueOf(page_res).getBytes());//更新最新的全局page值

            lock.release();

            return page;

        } catch (Exception e) {
            e.printStackTrace();

            return Integer.MIN_VALUE;

        }
    }

}
