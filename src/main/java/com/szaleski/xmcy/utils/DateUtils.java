package com.szaleski.xmcy.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static LocalDateTime getLocalDateTime(String milisStr) {
        long milis = Long.parseLong(milisStr);
        return Instant.ofEpochMilli(milis)
                      .atZone(ZoneId.systemDefault())
                      .toLocalDateTime();
    }

    public static LocalDateTime getLocalDateTime(Date date) {
        return date.toInstant()
                   .atZone(ZoneId.systemDefault())
                   .toLocalDateTime();
    }

    public static String toDateString(Date date) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }
    public static String getMonthName(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return new SimpleDateFormat("MMMM").format(calendar.getTime());
    }

}
