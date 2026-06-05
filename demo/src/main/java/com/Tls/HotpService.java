package com.Tls;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;

public class HotpService {

    public String generate(String secret, long counter) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA1"));

            byte[] data = ByteBuffer.allocate(8).putLong(counter).array();
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0xf;

            int binary =
                    ((hash[offset] & 0x7f) << 24) |
                            ((hash[offset + 1] & 0xff) << 16) |
                            ((hash[offset + 2] & 0xff) << 8) |
                            (hash[offset + 3] & 0xff);

            int otp = binary % 1_000_000;

            return String.format("%06d", otp);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}