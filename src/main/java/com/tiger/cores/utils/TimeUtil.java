package com.tiger.cores.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    private static final String ZONE_ID = "UTC";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static String convertToUTC(LocalDateTime localDateTime, String timeZone) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(timeZone));
        ZonedDateTime utcDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of(ZONE_ID));
        return utcDateTime.format(formatter);
    }

    public static LocalDateTime convertToLocalTime(String utcTime, String timeZone) {
        ZonedDateTime utcZonedDateTime = ZonedDateTime.parse(utcTime, formatter.withZone(ZoneId.of(ZONE_ID)));
        ZonedDateTime localZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.of(timeZone));
        return localZonedDateTime.toLocalDateTime();
    }
}
