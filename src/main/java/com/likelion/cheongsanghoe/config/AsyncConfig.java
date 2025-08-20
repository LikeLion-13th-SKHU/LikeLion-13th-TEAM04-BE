package com.likelion.cheongsanghoe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    // 비동기 활성화

    @Bean("aiExecutor")
    public Executor aiExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8); // 동시에 최소 8개 작업 처리
        executor.setMaxPoolSize(16); // 최대 16개까지 확장
        executor.setQueueCapacity(200); // 대기열 크기
        executor.setThreadNamePrefix("ai-");
        executor.initialize();
        return executor;
    }
}
