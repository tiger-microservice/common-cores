package com.tiger.cores.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiger.cores.constants.AppConstants;
import com.tiger.cores.dtos.responses.ApiResponse;
import com.tiger.cores.dtos.responses.InitSecureResponse;
import com.tiger.cores.entities.MasterConfig;
import com.tiger.cores.exceptions.BusinessLogicException;
import com.tiger.cores.exceptions.ErrorCode;
import com.tiger.cores.repositories.ConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecureService {

    private final ObjectMapper objectMapper;
    private final RedisService redisService;
    private final ConfigRepository configRepository;

    // Temporary storage
    public static final String AES_KEY = "NFNsJ2Iox08ZlfDt9KiGLer7cNNoMRnqmtquSCEE0D0=";
    public static final String IV = "3ccb508381494a44";

    public ApiResponse<?> initSecureProcess(String data) {
        try {
            // Decode and create public key object
            byte[] keyBytes = Base64.getDecoder().decode(data);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(spec);

            // Generate AES key
            String aesKey = SecureService.generateKey();

            String initSecureId = UUID.randomUUID().toString();

            // Save AES key to Redis
            redisService.put(initSecureId, aesKey, 30000);

            MasterConfig config = configRepository.findByConfigName(AppConstants.FLAG_SECURE);

            // Encrypt response with RSA public key
            InitSecureResponse response = InitSecureResponse.builder()
                    .iss(config != null && "ON".equals(config.getConfigValue()))
                    .sgId(initSecureId)
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

    /**
     * Used to decrypt request body
     */
    public String decryptBody(String aesKey, String encryptedData, String sgId) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(aesKey);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParams = new IvParameterSpec(sgId.substring(0, 16).getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParams);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessLogicException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    /**
     * Used to encrypt response body
     */
    public String encryptBody(String aesKey, Object data, String sgId) {
        try {
            byte[] aesKeyBytes = Base64.getDecoder().decode(aesKey);
            SecretKeySpec secretKeySpec = new SecretKeySpec(aesKeyBytes, "AES");
            IvParameterSpec ivParams = new IvParameterSpec(sgId.substring(0, 16).getBytes(StandardCharsets.UTF_8));
            Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParams);
            byte[] responseBytes = objectMapper.writeValueAsBytes(data);
            byte[] encryptedResponseBytes = aesCipher.doFinal(responseBytes);
            return Base64.getEncoder().encodeToString(encryptedResponseBytes);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessLogicException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    /**
     * Used to encrypt string fields
     */
    public static String encrypt(String data) {
        try {
            byte[] aesKeyBytes = Base64.getDecoder().decode(AES_KEY);
            SecretKeySpec secretKeySpec = new SecretKeySpec(aesKeyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParams = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParams);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessLogicException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    /**
     * Used to decrypt string fields
     */
    public static String decrypt(String encryptedData) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(AES_KEY);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParams = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParams);
            byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedData);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessLogicException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    /**
     * Generate AES key
     */
    public static String generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            return Base64.getEncoder().encodeToString(keyGenerator.generateKey().getEncoded());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessLogicException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public static void encryptFields(Object obj, String[] fields) throws IllegalAccessException {
        if (fields.length == 0) return;
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType().equals(String.class)) {
                for (String fieldName : fields) {
                    if (field.getName().equals(fieldName)) {
                        String originalValue = (String) field.get(obj);
                        if (originalValue != null) {
                            String encryptedValue = encrypt(originalValue);
                            field.set(obj, encryptedValue);
                        }
                    }
                }
            } else {
                Object nestedObject = field.get(obj);
                if (nestedObject != null) {
                    encryptFields(nestedObject, fields);
                }
            }
        }
    }

    public static void decryptFields(Object obj, String[] fields) throws IllegalAccessException {
        if (fields.length == 0) return;
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType().equals(String.class)) {
                for (String fieldName : fields) {
                    if (field.getName().equals(fieldName)) {
                        String encryptedValue = (String) field.get(obj);
                        if (encryptedValue != null) {
                            String decryptedValue = decrypt(encryptedValue);
                            field.set(obj, decryptedValue);
                        }
                    }
                }
            } else {
                Object nestedObject = field.get(obj);
                if (nestedObject != null) {
                    decryptFields(nestedObject, fields);
                }
            }
        }
    }
}
