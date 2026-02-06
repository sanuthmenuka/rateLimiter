package com.sanuth.rateLimiter.web;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sanuth.rateLimiter.limiter.RateLimitDecision;
import com.sanuth.rateLimiter.limiter.RateLimitRequest;
import com.sanuth.rateLimiter.limiter.RateLimiterPort;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    private static final String HEADER_LIMIT = "X-Ratelimit-Limit";
    private static final String HEADER_REMAINING = "X-Ratelimit-Remaining";
    private static final String HEADER_RETRY_AFTER = "X-Ratelimit-Retry-After";

    private final RateLimiterPort rateLimiter;
    private final RateLimiterProperties properties;

    public RateLimiterFilter(RateLimiterPort rateLimiter, RateLimiterProperties properties) {
        this.rateLimiter = rateLimiter;
        this.properties = properties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String key = resolveIdentity(request);
        RateLimitDecision decision = rateLimiter.evaluate(
                new RateLimitRequest(key, properties.getRequestsPerWindow(), properties.getWindowSeconds()));

        response.setHeader(HEADER_LIMIT, String.valueOf(decision.limit()));
        response.setHeader(HEADER_REMAINING, String.valueOf(decision.remaining()));
        response.setHeader(HEADER_RETRY_AFTER, String.valueOf(decision.retryAfterSeconds()));

        if (!decision.allowed()) {
            response.setStatus(429);
            response.setContentType("text/plain");
            response.getWriter().write("too_many_requests");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveIdentity(HttpServletRequest request) {
        String headerValue = request.getHeader(properties.getIdentityHeader());
        if (headerValue != null && !headerValue.isBlank()) {
            return headerValue;
        }
        return request.getRemoteAddr();
    }
}
