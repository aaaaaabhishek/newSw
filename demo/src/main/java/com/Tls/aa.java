//package com.Tls;
//
//public class aa {
//    import java.time.LocalDateTime;
//
//    public String verifyOtp(Long userId, String inputOtp) throws Exception {
//
//        // 🪣 Flood protection
//        Bucket bucket = rateLimiter.resolveBucket(userId);
//
//        if (!bucket.tryConsume(1)) {
//            return "TOO MANY REQUESTS";
//        }
//
//        User user = userRepository.findById(userId);
//        LocalDateTime now = LocalDateTime.now();
//
//        // 🔒 Check DB lock
//        if (user.getLockedUntil() != null &&
//                now.isBefore(user.getLockedUntil())) {
//            return "ACCOUNT LOCKED";
//        }
//
//        // Validate TOTP
//        boolean valid =
//                inputOtp.equals(TOTPUtil.generateWithOffset(user.getTotpSecret(), 0)) ||
//                        inputOtp.equals(TOTPUtil.generateWithOffset(user.getTotpSecret(), -1)) ||
//                        inputOtp.equals(TOTPUtil.generateWithOffset(user.getTotpSecret(), 1));
//
//        if (valid) {
//
//            user.setFailedAttempts(0);
//            user.setLockedUntil(null);
//            userRepository.update(user);
//
//            return "SUCCESS";
//        }
//
//        // ❌ Wrong OTP
//        user.setFailedAttempts(user.getFailedAttempts() + 1);
//
//        if (user.getFailedAttempts() >= 5) {
//            user.setLockedUntil(now.plusMinutes(15));
//        }
//
//        userRepository.update(user);
//
//        return "INVALID OTP";
//    }
//}
//
//
//import java.time.LocalDateTime;
//
//public String verifyOtp(Long userId, String inputOtp) throws Exception {
//
//    // 🪣 Flood protection
//    Bucket bucket = rateLimiter.resolveBucket(userId);
//
//    if (!bucket.tryConsume(1)) {
//        return "TOO MANY REQUESTS";
//    }
//
//    User user = userRepository.findById(userId);
//    LocalDateTime now = LocalDateTime.now();
//
//    // 🔒 Check DB lock
//    if (user.getLockedUntil() != null &&
//            now.isBefore(user.getLockedUntil())) {
//        return "ACCOUNT LOCKED";
//    }
//
//    // Validate TOTP
//    boolean valid =
//            inputOtp.equals(TOTPUtil.generateWithOffset(user.getTotpSecret(), 0)) ||
//                    inputOtp.equals(TOTPUtil.generateWithOffset(user.getTotpSecret(), -1)) ||
//                    inputOtp.equals(TOTPUtil.generateWithOffset(user.getTotpSecret(), 1));
//
//    if (valid) {
//
//        user.setFailedAttempts(0);
//        user.setLockedUntil(null);
//        userRepository.update(user);
//
//        return "SUCCESS";
//    }
//
//    // ❌ Wrong OTP
//    user.setFailedAttempts(user.getFailedAttempts() + 1);
//
//    if (user.getFailedAttempts() >= 5) {
//        user.setLockedUntil(now.plusMinutes(15));
//    }
//
//    userRepository.update(user);
//
//    return "INVALID OTP";
//}
//
//import io.github.bucket4j.*;
//        import java.time.Duration;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class RateLimiterService {
//
//    private final ConcurrentHashMap<Long, Bucket> buckets = new ConcurrentHashMap<>();
//
//    public Bucket resolveBucket(Long userId) {
//
//        return buckets.computeIfAbsent(userId, id ->
//                Bucket4j.builder()
//                        .addLimit(Bandwidth.simple(
//                                5,                         // 5 requests
//                                Duration.ofMinutes(1)      // per minute
//                        ))
//                        .build()
//        );
//    }
//}