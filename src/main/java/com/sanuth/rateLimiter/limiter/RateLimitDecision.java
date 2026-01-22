package com.sanuth.rateLimiter.limiter;

public record RateLimitDecision(boolean allowed, long remaining, long retryAfterSeconds, long limit) {
}
