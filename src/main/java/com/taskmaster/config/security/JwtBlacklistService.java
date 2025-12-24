package com.taskmaster.config.security;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(JwtBlacklistService.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String PREFIX = "blacklist:";

    // in-memory fallback: token -> expiryMillis
    private final Map<String, Long> inMemoryBlacklist = new ConcurrentHashMap<>();

    public void blacklistToken(String token) {
        Date expiration = jwtTokenProvider.extractExpiration(token);
        long ttl = expiration.getTime() - System.currentTimeMillis();

        if (ttl <= 0) {
            // already expired or invalid; nothing to do
            return;
        }

        String key = PREFIX + token;

        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue()
                        .set(key, "LOGOUT", Duration.ofMillis(ttl));
                // also remove any in-memory entry if present
                inMemoryBlacklist.remove(key);
                return;
            }
        } catch (Exception ex) {
            log.warn("Failed to write blacklist to Redis, using in-memory fallback: {}", ex.toString());
            // fall through to in-memory fallback
        }

        // in-memory fallback (non-distributed)
        long expiryMillis = System.currentTimeMillis() + ttl;
        inMemoryBlacklist.put(key, expiryMillis);
    }

    public boolean isBlacklisted(String token) {
        String key = PREFIX + token;

        // First try Redis (preferred)
        try {
            if (redisTemplate != null) {
                Boolean has = redisTemplate.hasKey(key);
                if (Boolean.TRUE.equals(has)) {
                    return true;
                }
                // if Redis is reachable and doesn't have the key, we can rely on it
                // but still fall back to in-memory in case the write happened locally before
            }
        } catch (Exception ex) {
            log.debug("Redis unavailable when checking blacklist (will consult in-memory fallback): {}", ex.toString());
        }

        // Check in-memory fallback: remove expired entries lazily
        Long expiry = inMemoryBlacklist.get(key);
        if (expiry == null) {
            return false;
        }
        if (expiry < System.currentTimeMillis()) {
            inMemoryBlacklist.remove(key);
            return false;
        }
        return true;
    }
}
