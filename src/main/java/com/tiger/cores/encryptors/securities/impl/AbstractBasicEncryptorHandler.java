package com.tiger.cores.encryptors.securities.impl;

import com.tiger.cores.encryptors.securities.EncryptorHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractBasicEncryptorHandler implements EncryptorHandler {

    public Object encrypt(String rawData, Object context) {
        return this.encrypt(rawData);
    }

    public String decrypt(String encryptedData, Object context) {
        return this.decrypt(encryptedData);
    }
}
