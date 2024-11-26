package com.example.events.configs;

import com.example.events.configs.RateLimitingFilter;
import com.example.events.exceptions.RateLimitExceededException;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class RateLimitingService {
    private final RateLimitingFilter rateLimitingFilter;

    public RateLimitingService(RateLimitingFilter rateLimitingFilter) {
        this.rateLimitingFilter = rateLimitingFilter;
    }

    public void checkRateLimit(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        if (!rateLimitingFilter.resolveBucket(clientIp).tryConsume(1)) {
            throw new RateLimitExceededException("Too many requests - please try again later.");
        }
    }
    }

