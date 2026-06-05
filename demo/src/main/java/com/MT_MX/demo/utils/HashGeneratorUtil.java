package com.MT_MX.demo.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashGeneratorUtil {
    private static final String SALT = "jkkkkkkkkkkkkkk";

    public static String generateHash(String data) throws Exception {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        String salted = SALT + data;

        byte[] hash = digest.digest(salted.getBytes(StandardCharsets.UTF_8));

        StringBuilder hex = new StringBuilder();
        for (byte b : hash) {
            hex.append(String.format("%02x", b));
        }

        return hex.toString();
    }
}
