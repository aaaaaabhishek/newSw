package com.Tls;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class AESGCMExample2 {

    public static void main(String[] args) throws Exception {
        String key = "12345678901234567890123456789012"; // 32 chars = 256-bit

        // ----- Decrypt -----
      String decrypted = decrypt("cyOK6vF4/Nu0CwiCoFBzoy2504frL/yiKLuOdk0DbYq3yVdFb19KvOkPy48=", key);
        System.out.println("Decrypted: " + decrypted);
    }


    // Decrypt method: extracts IV automatically
    public static String decrypt(String base64Combined, String key) throws Exception {
        byte[] combined = Base64.getDecoder().decode(base64Combined);

        byte[] iv = Arrays.copyOfRange(combined, 0, 12);
        byte[] ciphertext = Arrays.copyOfRange(combined, 12, combined.length);

        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

        byte[] decrypted = cipher.doFinal(ciphertext);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}