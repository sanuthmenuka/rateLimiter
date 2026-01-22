package com.sanuth.rateLimiter.limiter;

public record RateLimitRequest(String key, int limit, int windowSeconds) {
}
