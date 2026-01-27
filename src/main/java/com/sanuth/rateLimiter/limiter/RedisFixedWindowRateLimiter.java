package com.sanuth.rateLimiter.limiter;

import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

@Service
public class RedisFixedWindowRateLimiter implements RateLimiterPort {

    private static final RedisScript<List> SCRIPT = RedisScript.of(
            "local current = redis.call('INCR', KEYS[1]) "
                    + "if current == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]) end "
                    + "local limit = tonumber(ARGV[2]) "
                    + "local allowed = 1 "
                    + "if current > limit then allowed = 0 end "
                    + "local ttl = redis.call('TTL', KEYS[1]) "
                    + "if ttl < 0 then ttl = tonumber(ARGV[1]) end "
                    + "local remaining = limit - current "
                    + "if remaining < 0 then remaining = 0 end "
                    + "return {allowed, remaining, ttl}",
            List.class);

    private final StringRedisTemplate redisTemplate;

    public RedisFixedWindowRateLimiter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public RateLimitDecision evaluate(RateLimitRequest request) {
        String key = "rl:" + request.key();
        try {
            List<?> result = redisTemplate.execute(
                    SCRIPT,
                    List.of(key),
                    String.valueOf(request.windowSeconds()),
                    String.valueOf(request.limit()));

            if (result == null || result.size() < 3) {
                return new RateLimitDecision(true, request.limit(), 0, request.limit());
            }

            boolean allowed = toLong(result.get(0)) == 1;
            long remaining = toLong(result.get(1));
            long retryAfter = toLong(result.get(2));
            return new RateLimitDecision(allowed, remaining, retryAfter, request.limit());
        } catch (Exception ex) {
            return new RateLimitDecision(true, request.limit(), 0, request.limit());
        }
    }

    private long toLong(Object value) {
        if (value instanceof Number n) {
            return n.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }
}
