package com.demo.server.common.util;

/**
 * 字符串工具类。
 */
public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    public static String trim(String s) {
        return s == null ? null : s.trim();
    }

    public static String defaultIfBlank(String s, String defaultValue) {
        return isBlank(s) ? defaultValue : s;
    }

    public static String defaultIfEmpty(String s, String defaultValue) {
        return isEmpty(s) ? defaultValue : s;
    }

    public static String truncate(String s, int maxLen) {
        if (s == null || s.length() <= maxLen) {
            return s;
        }
        return s.substring(0, maxLen);
    }

    public static String truncateWithEllipsis(String s, int maxLen) {
        if (s == null || s.length() <= maxLen) {
            return s;
        }
        if (maxLen <= 3) {
            return "...".substring(0, maxLen);
        }
        return s.substring(0, maxLen - 3) + "...";
    }

    public static String repeat(String s, int times) {
        if (s == null) {
            return null;
        }
        if (times <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(s.length() * times);
        for (int i = 0; i < times; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static String padStart(String s, int length, char padChar) {
        if (s == null) {
            s = "";
        }
        if (s.length() >= length) {
            return s;
        }
        StringBuilder sb = new StringBuilder(length);
        int padCount = length - s.length();
        for (int i = 0; i < padCount; i++) {
            sb.append(padChar);
        }
        sb.append(s);
        return sb.toString();
    }

    public static String padEnd(String s, int length, char padChar) {
        if (s == null) {
            s = "";
        }
        if (s.length() >= length) {
            return s;
        }
        StringBuilder sb = new StringBuilder(length);
        sb.append(s);
        int padCount = length - s.length();
        for (int i = 0; i < padCount; i++) {
            sb.append(padChar);
        }
        return sb.toString();
    }

    public static String capitalize(String s) {
        if (isEmpty(s)) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static String uncapitalize(String s) {
        if (isEmpty(s)) {
            return s;
        }
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    public static String camelToSnake(String s) {
        if (isEmpty(s)) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s.length() + 4);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String snakeToCamel(String s) {
        if (isEmpty(s)) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperNext = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '_') {
                upperNext = true;
            } else if (upperNext) {
                sb.append(Character.toUpperCase(c));
                upperNext = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String mask(String s, int prefixLen, int suffixLen) {
        if (s == null) {
            return null;
        }
        if (s.length() <= prefixLen + suffixLen) {
            return "****";
        }
        String prefix = s.substring(0, prefixLen);
        String suffix = s.substring(s.length() - suffixLen);
        return prefix + "****" + suffix;
    }

    public static String reverse(String s) {
        if (s == null) {
            return null;
        }
        return new StringBuilder(s).reverse().toString();
    }

    public static boolean equals(String a, String b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }

    public static boolean equalsIgnoreCase(String a, String b) {
        if (a == null) {
            return b == null;
        }
        return a.equalsIgnoreCase(b);
    }

    public static boolean contains(String haystack, String needle) {
        if (haystack == null || needle == null) {
            return false;
        }
        return haystack.contains(needle);
    }

    public static boolean containsIgnoreCase(String haystack, String needle) {
        if (haystack == null || needle == null) {
            return false;
        }
        return haystack.toLowerCase().contains(needle.toLowerCase());
    }

    public static boolean startsWith(String s, String prefix) {
        if (s == null || prefix == null) {
            return false;
        }
        return s.startsWith(prefix);
    }

    public static boolean endsWith(String s, String suffix) {
        if (s == null || suffix == null) {
            return false;
        }
        return s.endsWith(suffix);
    }
}
