package com.szaleski.xmcy.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    public static LocalDate toLocalDate(Date date) {
        return date.toInstant()
                   .atZone(ZoneId.systemDefault())
                   .toLocalDate();
    }

    public static String toDateString(LocalDate date) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dateTimeFormatter.format(date);
    }

}
