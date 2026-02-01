package com.sanuth.rateLimiter.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterProperties {

    private int requestsPerWindow = 10;
    private int windowSeconds = 60;
    private String identityHeader = "X-Client-Id";

    public int getRequestsPerWindow() {
        return requestsPerWindow;
    }

    public void setRequestsPerWindow(int requestsPerWindow) {
        this.requestsPerWindow = requestsPerWindow;
    }

    public int getWindowSeconds() {
        return windowSeconds;
    }

    public void setWindowSeconds(int windowSeconds) {
        this.windowSeconds = windowSeconds;
    }

    public String getIdentityHeader() {
        return identityHeader;
    }

    public void setIdentityHeader(String identityHeader) {
        this.identityHeader = identityHeader;
    }
}
