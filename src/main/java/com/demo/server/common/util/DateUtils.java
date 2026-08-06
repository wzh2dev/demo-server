package com.demo.server.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间工具类。
 */
public final class DateUtils {

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("Asia/Shanghai");

    private DateUtils() {
    }

    public static String format(Long timestamp) {
        return format(timestamp, DEFAULT_FORMAT);
    }

    public static String format(Long timestamp, String pattern) {
        if (timestamp == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(DEFAULT_TIME_ZONE);
        return sdf.format(new Date(timestamp));
    }

    public static Long parse(String dateStr, String pattern) {
        if (dateStr == null) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            sdf.setTimeZone(DEFAULT_TIME_ZONE);
            return sdf.parse(dateStr).getTime();
        } catch (Exception e) {
            return null;
        }
    }

    public static Long parseDefault(String dateStr) {
        return parse(dateStr, DEFAULT_FORMAT);
    }

    public static String now() {
        return format(System.currentTimeMillis());
    }

    public static String today() {
        return format(System.currentTimeMillis(), DATE_FORMAT);
    }

    public static long startOfDay(Long timestamp) {
        if (timestamp == null) {
            return 0L;
        }
        return (timestamp / 86_400_000L) * 86_400_000L;
    }

    public static long endOfDay(Long timestamp) {
        if (timestamp == null) {
            return 0L;
        }
        return startOfDay(timestamp) + 86_400_000L - 1;
    }

    public static long plusDays(Long timestamp, int days) {
        if (timestamp == null) {
            return 0L;
        }
        return timestamp + (long) days * 86_400_000L;
    }

    public static long minusDays(Long timestamp, int days) {
        return plusDays(timestamp, -days);
    }

    public static long plusHours(Long timestamp, int hours) {
        if (timestamp == null) {
            return 0L;
        }
        return timestamp + (long) hours * 3_600_000L;
    }

    public static long plusMinutes(Long timestamp, int minutes) {
        if (timestamp == null) {
            return 0L;
        }
        return timestamp + (long) minutes * 60_000L;
    }

    public static long diffDays(Long t1, Long t2) {
        if (t1 == null || t2 == null) {
            return 0L;
        }
        return (t1 - t2) / 86_400_000L;
    }

    public static long diffHours(Long t1, Long t2) {
        if (t1 == null || t2 == null) {
            return 0L;
        }
        return (t1 - t2) / 3_600_000L;
    }
}
