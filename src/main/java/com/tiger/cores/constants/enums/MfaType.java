package com.tiger.cores.constants.enums;

import lombok.Getter;

@Getter
public enum MfaType {
    EMAIL("email", "Mfa email"),
    SMS("sms", "Mfa sms");

    private final String code;
    private final String description;

    MfaType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
