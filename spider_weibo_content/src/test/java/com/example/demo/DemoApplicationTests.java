package com.example.demo;

import com.example.demo.kafka.KafkaService;
import com.example.demo.nlp.Analyser;
import com.example.demo.nlp.TokenHelper;
import com.example.demo.spider.SpiderForContent;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class DemoApplicationTests {
    @Value("${zk.localPath}")
    public String lockPath;

    @Value("${zk.url}")
    public String url;

    int k = 0;

    //InterProcessLock lock = new InterProcessMutex(zkClient, lockPath);

    private CuratorFramework getZkClient() {
        String zkServerAddress = url;
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3, 5000);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString(zkServerAddress)
                .sessionTimeoutMs(5000*4)
                .connectionTimeoutMs(5000*4)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();

        return zkClient;
    }

    @Test
    void contextLoads() throws Exception {
        CuratorFramework zkClient = getZkClient();
        String lockPath = "/lock";
        InterProcessMutex lock = new InterProcessMutex(zkClient, lockPath);
        //模拟100个线程抢锁
        for (int i = 0; i < 100; i++) {
            new Thread(new TestThread(i, lock)).start();
        }
    }

    @Autowired
    KafkaService kafkaService;

    @Test
    void Send(){
        kafkaService.sendMessageToKafka("hello_world");
    }

    @Autowired
    TokenHelper tokenHelper;

    @Autowired
    Analyser analyser;


    @Test
    void TestNLP() throws InterruptedException {
        tokenHelper.getAccessToken();

        analyser.analyse("苹果是一家伟大的公司");
    }

    @Autowired
    SpiderForContent spiderForContent;

    @Test
    void t() throws IOException, InterruptedException {
        spiderForContent.start();
    }

    static class TestThread implements Runnable {
        private Integer threadFlag;
        private InterProcessMutex lock;

        public TestThread(Integer threadFlag, InterProcessMutex lock) {
            this.threadFlag = threadFlag;
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                lock.acquire();
                System.out.println("第"+threadFlag+"线程获取到了锁");
                //等到1秒后释放锁
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    lock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
