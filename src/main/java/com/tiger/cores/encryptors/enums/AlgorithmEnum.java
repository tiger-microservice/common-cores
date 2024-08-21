package com.tiger.cores.encryptors.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlgorithmEnum {
    RSA("RSA"),
    AES("AES");
    private final String value;
}
