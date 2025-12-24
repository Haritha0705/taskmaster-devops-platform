package com.taskmaster.config.redis;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TestInMemoryRateLimiter extends RateLimiterService {

    private final ConcurrentHashMap<String, AtomicInteger> map = new ConcurrentHashMap<>();
    private final int limit = 100; // keep in sync with test properties

    // call super with null RedisTemplate, we'll override behavior
    public TestInMemoryRateLimiter() {
        super(null);
    }

    @Override
    public boolean isAllowed(String key) {
        AtomicInteger v = map.computeIfAbsent(key, k -> new AtomicInteger(0));
        int c = v.incrementAndGet();
        return c <= limit;
    }
}

