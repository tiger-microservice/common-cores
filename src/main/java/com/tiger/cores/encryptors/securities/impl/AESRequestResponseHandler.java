package com.tiger.cores.encryptors.securities.impl;

import com.tiger.cores.constants.AppConstants;
import com.tiger.cores.dtos.responses.ApiResponse;
import com.tiger.cores.dtos.responses.InitSecureResponse;
import com.tiger.cores.encryptors.constants.HttpRequestAttributeConstants;
import com.tiger.cores.encryptors.constants.SecureConstants;
import com.tiger.cores.encryptors.enums.CypherEnum;
import com.tiger.cores.exceptions.BusinessLogicException;
import com.tiger.cores.exceptions.ErrorCode;
import com.tiger.cores.exceptions.SecureLogicException;
import com.tiger.cores.services.impl.RedisService;
import com.tiger.cores.utils.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class AESRequestResponseHandler extends AbstractBasicEncryptorHandler {

    final RedisService redisService;
    final HttpServletRequest httpRequest;

    @Override
    public Object encrypt(String rawData) {
        if (StringUtils.isBlank(rawData)) {
            return null;
        }

        InitSecureResponse encryptionKeyData = getEncryptionKeyData();
        if (!isEnableEncryption(encryptionKeyData)) {
            return rawData;
        }

        return ApiResponse.responseOK(encryptBody(encryptionKeyData.getEak(), rawData));
    }

    private InitSecureResponse getEncryptionKeyData() {
        var appKeyValue = httpRequest.getAttribute(HttpRequestAttributeConstants.SECURED_AES_VALUE);
        if (Objects.nonNull(appKeyValue)) {
            return JsonUtil.castToObject((String) appKeyValue, InitSecureResponse.class);
        }

        String appKey = httpRequest.getHeader(AppConstants.APP_TRANSACTION_KEY);
        if (StringUtils.isBlank(appKey)) {
            throw new SecureLogicException(ErrorCode.SECURE_INVALID);
        }

        String value = (String) redisService.get(SecureConstants.SECURE_KEY + appKey);
        httpRequest.setAttribute(HttpRequestAttributeConstants.SECURED_AES_VALUE, value);

        return JsonUtil.castToObject(value, InitSecureResponse.class);
    }

    @Override
    public String decrypt(String encryptedData) {
        InitSecureResponse encryptionKeyData = getEncryptionKeyData();
        if (Boolean.FALSE.equals(isEnableEncryption(encryptionKeyData))) {
            return encryptedData;
        }

        EncryptedRequest encryptedRequest = JsonUtil.castToObject(encryptedData, EncryptedRequest.class);
        if (StringUtils.isBlank(encryptedRequest.getData())) {
            throw new SecureLogicException(ErrorCode.SECURE_INVALID);
        }

        return decryptBody(encryptionKeyData.getEak(), encryptedRequest.getData());
    }

    private boolean isEnableEncryption(InitSecureResponse encryptionKeyData) {
        return Objects.nonNull(encryptionKeyData)
                && encryptionKeyData.isIss()
                && Objects.nonNull(encryptionKeyData.getEak());
    }

    /**
     * Used to decrypt request body
     */
    public String decryptBody(AESEncryptionKey aesKey, String encryptedData) {
        try {
            if (StringUtils.isBlank(encryptedData)) {
                return encryptedData;
            }

            Cipher cipher = Cipher.getInstance(CypherEnum.AES_VALUE.getValue());
            cipher.init(Cipher.DECRYPT_MODE, aesKey.buildSecretKeySpec(), aesKey.buildIVSecureRandom());
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(original);
        } catch (Exception e) {
            log.error("[decryptBody] error {}", e.getMessage(), e);
            throw new BusinessLogicException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    /**
     * Used to encrypt response body
     */
    public String encryptBody(AESEncryptionKey aesKey, String rawData) {
        try {
            if (StringUtils.isBlank(rawData)) {
                return rawData;
            }

            Cipher aesCipher = Cipher.getInstance(CypherEnum.AES_VALUE.getValue());
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey.buildSecretKeySpec(), aesKey.buildIVSecureRandom());
            byte[] encryptedResponseBytes = aesCipher.doFinal(rawData.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedResponseBytes);
        } catch (Exception e) {
            log.error("[encryptBody] error {}", e.getMessage(), e);
            throw new BusinessLogicException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }
}
