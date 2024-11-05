package com.tiger.cores.configs.timezone;

public class TimezoneContext {

    private static final ThreadLocal<String> CURRENT_TIMEZONE = new ThreadLocal<>();

    public static String getCurrentTimezone() {
        return CURRENT_TIMEZONE.get();
    }

    public static void setCurrentTimezone(String timezone) {
        CURRENT_TIMEZONE.set(timezone);
    }

    public static void clear() {
        CURRENT_TIMEZONE.remove();
    }
}
