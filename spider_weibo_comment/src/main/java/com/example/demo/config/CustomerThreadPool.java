package com.example.demo.config;

import com.example.demo.domain.Weibo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class CustomerThreadPool {
    static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20,40,60L,TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());

    static LinkedBlockingQueue<Weibo> queue = new LinkedBlockingQueue<Weibo>(100);

    @Bean
    public ThreadPoolExecutor getThreadPool(){
        return threadPool;
    }

    @Bean
    public LinkedBlockingQueue getQueue(){
        return queue;
    }
}
