package com.Tls;

import io.github.bucket4j.*;
import com.github.benmanes.caffeine.cache.*;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class RateLimiterService {

    private final Cache<String, Bucket> cache = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .maximumSize(200_000)
            .build();

    private Bucket newBucket(String key) {

        // 🔥 SEND LIMITS
        if (key.startsWith("SEND_GLOBAL")) {
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(1000,
                            Refill.greedy(1000, Duration.ofMinutes(1))))
                    .build();
        }

        if (key.startsWith("SEND_IP")) {
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(20,
                            Refill.greedy(20, Duration.ofMinutes(1))))
                    .build();
        }

        if (key.startsWith("SEND_USER")) {
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(5,
                            Refill.greedy(5, Duration.ofMinutes(1))))
                    .build();
        }

        // 🔥 VERIFY LIMITS (STRONGER)
        if (key.startsWith("VERIFY_GLOBAL")) {
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(2000,
                            Refill.greedy(2000, Duration.ofMinutes(1))))
                    .build();
        }

        if (key.startsWith("VERIFY_IP")) {
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(50,
                            Refill.greedy(50, Duration.ofMinutes(1))))
                    .build();
        }

        if (key.startsWith("VERIFY_USER")) {
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(10,
                            Refill.greedy(10, Duration.ofMinutes(1))))
                    .build();
        }

        throw new IllegalArgumentException("Unknown key: " + key);
    }

    public boolean allow(String key) {
        Bucket bucket = cache.get(key, this::newBucket);
        return bucket.tryConsume(1);
    }
}