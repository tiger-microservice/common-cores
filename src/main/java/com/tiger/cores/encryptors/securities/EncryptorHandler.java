package com.tiger.cores.encryptors.securities;

public interface EncryptorHandler {

    Object encrypt(String rawData);

    String decrypt(String encryptedData);

    Object encrypt(String rawData, Object context);

    String decrypt(String encryptedData, Object context);
}
