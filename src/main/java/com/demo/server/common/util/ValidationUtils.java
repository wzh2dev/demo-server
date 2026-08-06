package com.demo.server.common.util;

import java.util.regex.Pattern;

/**
 * 参数校验工具类。
 */
public final class ValidationUtils {

    private static final Pattern MOBILE_PATTERN =
            Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern URL_PATTERN =
            Pattern.compile("^https?://[A-Za-z0-9._~:/?#\\[\\]@!$&'()*+,;=%-]+$");
    private static final Pattern NUMBER_PATTERN =
            Pattern.compile("^-?\\d+(\\.\\d+)?$");
    private static final Pattern POSITIVE_INT_PATTERN =
            Pattern.compile("^[1-9]\\d*$");

    private ValidationUtils() {
    }

    public static boolean isNullOrBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isNotNullOrBlank(String s) {
        return !isNullOrBlank(s);
    }

    public static boolean isMobile(String s) {
        return s != null && MOBILE_PATTERN.matcher(s).matches();
    }

    public static boolean isEmail(String s) {
        return s != null && EMAIL_PATTERN.matcher(s).matches();
    }

    public static boolean isUrl(String s) {
        return s != null && URL_PATTERN.matcher(s).matches();
    }

    public static boolean isNumber(String s) {
        return s != null && NUMBER_PATTERN.matcher(s).matches();
    }

    public static boolean isPositiveInt(String s) {
        return s != null && POSITIVE_INT_PATTERN.matcher(s).matches();
    }

    public static boolean inRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    public static boolean inRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public static boolean inRange(long value, long min, long max) {
        return value >= min && value <= max;
    }

    public static boolean maxLength(String s, int max) {
        return s == null || s.length() <= max;
    }

    public static boolean minLength(String s, int min) {
        return s != null && s.length() >= min;
    }

    public static boolean lengthBetween(String s, int min, int max) {
        if (s == null) {
            return false;
        }
        return s.length() >= min && s.length() <= max;
    }

    public static void requireNonNullOrBlank(String s, String name) {
        if (isNullOrBlank(s)) {
            throw new IllegalArgumentException(name + " must not be null or blank");
        }
    }

    public static void requirePositive(Long value, String name) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(name + " must be positive");
        }
    }

    public static void requirePositive(Integer value, String name) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(name + " must be positive");
        }
    }

    public static void requireNonNegative(Integer value, String name) {
        if (value == null || value < 0) {
            throw new IllegalArgumentException(name + " must be non-negative");
        }
    }

    public static void requireNonNegative(Double value, String name) {
        if (value == null || value < 0) {
            throw new IllegalArgumentException(name + " must be non-negative");
        }
    }
}
