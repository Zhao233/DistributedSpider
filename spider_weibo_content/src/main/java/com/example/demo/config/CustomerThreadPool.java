package com.example.demo.config;

import com.example.demo.domain.WeiBo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class CustomerThreadPool {
    static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5,10,60L,TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());

    static LinkedBlockingQueue<WeiBo> queue = new LinkedBlockingQueue<WeiBo>(100);

    @Bean
    public ThreadPoolExecutor getThreadPool(){
        return threadPool;
    }

    @Bean
    public LinkedBlockingQueue getQueue(){
        return queue;
    }
}
