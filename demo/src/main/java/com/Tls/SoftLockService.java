package com.Tls;

import com.github.benmanes.caffeine.cache.*;
import java.util.concurrent.TimeUnit;

public class SoftLockService {

    static class Attempt {
        int count;
        long lockUntil;
    }

    private final Cache<String, Attempt> cache = Caffeine.newBuilder()
            .expireAfterWrite(20, TimeUnit.MINUTES)
            .maximumSize(100_000)
            .build();

    private static final int MAX = 3;
    private static final long LOCK_TIME = 15 * 60 * 1000;

    public boolean isLocked(String userId) {
        Attempt a = cache.getIfPresent(userId);
        return a != null && System.currentTimeMillis() < a.lockUntil;
    }

    public void fail(String userId) {
        Attempt a = cache.get(userId, k -> new Attempt());

        a.count++;

        if (a.count >= MAX) {
            a.lockUntil = System.currentTimeMillis() + LOCK_TIME;
            a.count = 0;
        }
    }

    public void success(String userId) {
        cache.invalidate(userId);
    }
}