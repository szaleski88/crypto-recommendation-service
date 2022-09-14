package com.szaleski.xmcy.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {

    private DateUtils() {
        // only static methods.
    }

    public static LocalDateTime strMillisToLocalDateTime(String milisStr) {
        long milis = Long.parseLong(milisStr);
        return Instant.ofEpochMilli(milis)
                      .atZone(ZoneId.systemDefault())
                      .toLocalDateTime();
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant()
                   .atZone(ZoneId.systemDefault())
                   .toLocalDateTime();
    }

    public static String toDateString(Date date) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

}
