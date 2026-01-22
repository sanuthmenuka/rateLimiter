package com.sanuth.rateLimiter.limiter;

public interface RateLimiterPort {
    RateLimitDecision evaluate(RateLimitRequest request);
}
