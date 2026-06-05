package com.Tls;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class AESGCMExample {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int GCM_IV_LENGTH = 12;

    public static void main(String[] args) throws Exception {
        String data = "Hello SMS Vendor";
        String key = "12345678901234567890123456789012"; // 32 chars = 256-bit

        // ----- Generate random IV -----
        byte[] iv = new byte[12]; // 12 bytes for GCM
        SECURE_RANDOM.nextBytes(iv);

        // ----- Encrypt -----
        String base64ToSend = encrypt(data, key, iv);
        System.out.println("Send to vendor (Base64 IV+ciphertext): " + base64ToSend);

    }

    // Encrypt method: returns Base64 of IV + ciphertext
    public static String encrypt(String plaintext, String key, byte[] iv) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        // Combine IV : ciphertext for transmission
        byte[] combined = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);

        return Base64.getEncoder().encodeToString(combined);
    }
}