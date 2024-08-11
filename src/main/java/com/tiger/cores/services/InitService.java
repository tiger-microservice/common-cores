package com.tiger.cores.services;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.Cipher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiger.cores.dtos.requests.BaseRequest;
import com.tiger.cores.dtos.responses.ApiResponse;
import com.tiger.cores.dtos.responses.InitResponse;
import com.tiger.cores.exceptions.BusinessLogicException;
import com.tiger.cores.exceptions.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class InitService {

    private final ObjectMapper objectMapper;
    private final RedisService redisService;

    @Value("${flag.secure:OFF}")
    private String flagSecure;

    @Transactional
    public ApiResponse<?> initProcess(BaseRequest<String> request) {
        try {
            // Decode and create public key object
            byte[] keyBytes = Base64.getDecoder().decode(request.getData());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(spec);

            // Generate AES key
            String aesKey = SecureService.generateKey();

            String initId = UUID.randomUUID().toString();

            // Save AES key to Redis
            redisService.put(initId, aesKey, 30000);

            // Encrypt response with RSA public key
            InitResponse response = InitResponse.builder()
                    .iss("ON".equals(flagSecure))
                    .sgId(initId)
                    .eak(aesKey)
                    .build();

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedData = cipher.doFinal(objectMapper.writeValueAsBytes(response));
            String encryptedDataBase64 = Base64.getEncoder().encodeToString(encryptedData);

            // Return encrypted data
            return ApiResponse.<String>builder().data(encryptedDataBase64).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessLogicException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }
}
