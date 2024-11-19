package com.tiger.cores.serializers.sensitive;

import java.util.function.Function;

public enum SensitiveStrategy {
    /**
     * Chiến lược nhạy cảm cho Username.
     */
    USERNAME(s -> s.replaceAll("(\\S)\\S(\\S*)", "$1*$2")),
    /**
     * Loại nhạy cảm cho chứng minh nhân dân.
     */
    ID_CARD(s -> s.replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1****$2")),
    /**
     * Loại nhạy cảm cho số điện thoại.
     */
    PHONE(s -> s.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")),
    /**
     * Loại nhạy cảm cho email.
     */
    EMAIL(s -> s.replaceAll("(^\\w)[^@]*(@.*$)", "$1****$2")),
    /**
     * Loại nhạy cảm cho địa chỉ.
     */
    ADDRESS(s -> s.replaceAll("(\\S{3})\\S{2}(\\S*)\\S{2}", "$1****$2****"));


    private final Function<String, String> maskingFunction;

    SensitiveStrategy(Function<String, String> maskingFunction) {
        this.maskingFunction = maskingFunction;
    }

    public Function<String, String> maskingFunction() {
        return maskingFunction;
    }
}
