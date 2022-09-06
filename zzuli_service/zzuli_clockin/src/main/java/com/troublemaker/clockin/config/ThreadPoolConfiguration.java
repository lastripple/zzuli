package com.troublemaker.clockin.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "data.pool")
public class ThreadPoolConfiguration {
    // corePoolSize     核心线程数
    // maximumPoolSize  最大线程数
    // keepAliveTime    空闲线程存活时间
    // unit             时间单位
    // workQueue        任务队列
    // threadFactory    线程工厂
    // handler          线程拒绝策略
    // 当任务数量超过 corePoolSize 时，任务进入等待队列等待，若超过等待队列 capacity，且此时线程数小于 maximumPoolSize，创建新线程。
    // 达到最大线程数量，后来的任务执行CallerRunsPolicy()策略, 即调用者(main)执行该任务
    private Integer corePoolSize;
    private Integer maximumPoolSize;
    private Integer keepAliveTime;
    private Integer capacity;

    @Bean
    public ThreadPoolExecutor getThreadPoolExecutor() {
        log.info("-----------------初始化线程池-------------------");
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<>(capacity), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
    }
}

