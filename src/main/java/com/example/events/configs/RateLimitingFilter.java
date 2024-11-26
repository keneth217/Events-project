package com.example.events.configs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import io.github.bucket4j.*;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
@Service
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    // Thread-safe map to store rate-limiting buckets per client
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    /**
     * Creates a new Bucket instance with rate-limiting rules.
     * Allows 5 requests per minute using Bucket4j's token-bucket algorithm.
     */
    private Bucket createNewBucket() {
        Refill refill = Refill.greedy(5, Duration.ofMinutes(1)); // 5 tokens per minute
        Bandwidth limit = Bandwidth.classic(5, refill);          // Apply refill strategy
        return Bucket.builder().addLimit(limit).build();
    }

    /**
     * Resolves or creates a Bucket for the given client IP.
     */
    public Bucket resolveBucket(String clientIp) {
        return bucketCache.computeIfAbsent(clientIp, key -> createNewBucket());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String clientIp = request.getRemoteAddr(); // Identify client by IP
        Bucket bucket = resolveBucket(clientIp);

        if (bucket.tryConsume(1)) { // Attempt to consume 1 token
            filterChain.doFilter(request, response); // Proceed with the request
        } else {
            // Rate limit exceeded
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests - please try again later.");
            response.getWriter().flush();
        }
    }
}
