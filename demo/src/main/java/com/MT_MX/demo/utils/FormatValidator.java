package com.MT_MX.demo.utils;
import java.util.regex.Pattern;

public final class FormatValidator {

    private FormatValidator() {}

    public static boolean matches(String value, String regex) {
        if (value == null) return false;
        return Pattern.matches(regex, value.trim());
    }
}