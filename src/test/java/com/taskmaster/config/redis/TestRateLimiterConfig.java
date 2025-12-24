package com.taskmaster.config.redis;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestRateLimiterConfig {

    @Bean
    @Primary
    public RateLimiterService rateLimiterService() {
        return new TestInMemoryRateLimiter();
    }
}
