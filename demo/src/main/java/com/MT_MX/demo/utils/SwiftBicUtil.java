package com.MT_MX.demo.utils;

import com.MT_MX.demo.ast.BlockNode;

public class SwiftBicUtil {
    private SwiftBicUtil(){};
    // ==========================================================
    // Extract BIC from Block 1
    // ==========================================================
    public static String extractBic(BlockNode b1) {

        if (b1 == null || b1.getContent() == null)
            return "";

        String content = b1.getContent();

        // Block1 format: F01BANKBEBBAXXX2222123456
        if (content.length() < 20)
            return content.substring(4, 15);
        String bic = "";
        String actualbic;
        String finalbic="";
        if (content.length() >=25 ) {
            bic = content.substring(3);
            actualbic = bic.substring(0, bic.length() - 10);
            if (actualbic.length() >= 12)
                finalbic = actualbic.substring(0, 8) + actualbic.substring(9);
            return finalbic;
        }
        return "";
    }
    public static String extractBic2(BlockNode b1) {

        if (b1 == null || b1.getContent() == null)
            return "";

        String content = b1.getContent();
        // Block2 format: I103BANKDEFFXXXXN
        if (content.length() < 18) {
            String kk=content.substring(4, 15);
            return kk;
        }
        String bic = "";
        String actualbic;
        String finalbic="";
        if (content.length() >=25 ) {
            bic = content.substring(4);
            actualbic = bic.substring(0, bic.length() - 1);
            if (actualbic.length() >= 12)
                finalbic = actualbic.substring(0, 8) + actualbic.substring(9);
            return finalbic;
        }
        return "";
    }

    public static String normalizeBic(String bic) {
        if (bic == null || bic.isBlank()) {
            return null;
        }
        bic = bic.trim().toUpperCase();
        // cut anything beyond 11 characters
        if (bic.length() > 11) {
            bic = bic.substring(0, 11);
        }
        // convert BIC8 to BIC11
        if (bic.length() == 8) {
            bic = bic + "XXX";
        }
        if (bic.length() != 11) {
            throw new RuntimeException("Invalid BIC: " + bic);
        }
        return bic;
    }
}
