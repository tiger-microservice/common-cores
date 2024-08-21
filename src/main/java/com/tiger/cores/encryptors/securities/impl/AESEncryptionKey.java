package com.tiger.cores.encryptors.securities.impl;

import java.security.Key;
import java.util.Base64;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AESEncryptionKey {
    private String k;
    private String i;

    public Key buildSecretKeySpec() {
        if (StringUtils.isBlank(this.k)) {
            return null;
        }

        return new SecretKeySpec(this.keyInByte(), "AES");
    }

    public IvParameterSpec buildIVSecureRandom() {
        if (StringUtils.isBlank(this.i)) {
            return null;
        }
        return new IvParameterSpec(this.ivInByte());
    }

    public byte[] keyInByte() {
        return Base64.getDecoder().decode(this.k);
    }

    public byte[] ivInByte() {
        return Base64.getDecoder().decode(this.i);
    }
}
