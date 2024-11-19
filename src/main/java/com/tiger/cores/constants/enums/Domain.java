package com.tiger.cores.constants.enums;

import lombok.Getter;

@Getter
public enum Domain {
    CMS("CMS"),
    TRELLO("TRELLO"),
    APP_CUSTOMER("APP_CUSTOMER"),
    CPM("CPM"),
    UNKNOWN("UNKNOWN"),
    ;

    private String clientName;

    Domain(String description) {
        this.clientName = description;
    }

    public String clientName() {
        return this.clientName;
    }

    public String value() {
        return this.name();
    }
}
