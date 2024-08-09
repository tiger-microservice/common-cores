package com.tiger.cores.constants.enums;

import lombok.Getter;

@Getter
public enum OnlineStatus {
    OFFLINE("offline", "Mfa email"),
    ONLINE("online", "Mfa sms"),
    NONE("none", "Mfa sms");

    private final String code;
    private final String description;

    OnlineStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
