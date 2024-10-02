package com.tiger.cores.constants.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum CheckPointType {
    ALL();

    private List<CheckPointField> fields;

    CheckPointType(CheckPointField... fields) {
        this.fields = Arrays.asList(fields);
    }
}
