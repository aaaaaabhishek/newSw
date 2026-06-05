package com.Tls;
import com.github.benmanes.caffeine.cache.*;
import java.util.concurrent.TimeUnit;

public class CooldownService {

    private final Cache<String, Long> cache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .maximumSize(100_000)
            .build();

    public boolean allow(String userId) {
        if (cache.getIfPresent(userId) != null) {
            return false;
        }
        cache.put(userId, System.currentTimeMillis());
        return true;
    }
}