package com.tiger.cores.constants.enums;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public enum CheckPointType {
    ALL();

    private List<CheckPointField> fields;

    CheckPointType(CheckPointField... fields) {
        this.fields = Arrays.asList(fields);
    }
}
