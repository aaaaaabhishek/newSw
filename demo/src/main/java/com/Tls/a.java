package com.Tls;
import io.github.bucket4j.*;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

    public class a {

        private final Map<String, Bucket> phoneBuckets = new ConcurrentHashMap<>();
        private final Map<String, Bucket> ipBuckets = new ConcurrentHashMap<>();

        private Bucket createPhoneBucket() {

            Bandwidth limit = Bandwidth.builder()
                    .capacity(5)
                    .refillIntervally(5, Duration.ofHours(1))
                    .build();

            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        }

        private Bucket createIpBucket() {

            Bandwidth limit = Bandwidth.builder()
                    .capacity(50)
                    .refillIntervally(50, Duration.ofHours(1))
                    .build();

            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        }

        public boolean allowPhone(String phone) {

            Bucket bucket = phoneBuckets.computeIfAbsent(phone, k -> createPhoneBucket());

            return bucket.tryConsume(1);
        }

        public boolean allowIp(String ip) {

            Bucket bucket = ipBuckets.computeIfAbsent(ip, k -> createIpBucket());

            return bucket.tryConsume(1);
        }
    }

