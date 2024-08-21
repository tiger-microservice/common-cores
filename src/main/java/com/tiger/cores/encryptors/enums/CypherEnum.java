package com.tiger.cores.encryptors.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CypherEnum {
    AES_VALUE("AES/CBC/PKCS5Padding");
    private final String value;
}
