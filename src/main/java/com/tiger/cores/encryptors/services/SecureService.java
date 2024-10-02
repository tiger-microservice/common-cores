package com.tiger.cores.encryptors.services;

import com.tiger.cores.dtos.responses.InitSecureResponse;
import com.tiger.cores.encryptors.constants.SecureConstants;
import com.tiger.cores.encryptors.enums.AlgorithmEnum;
import com.tiger.cores.encryptors.enums.CypherEnum;
import com.tiger.cores.encryptors.securities.impl.AESEncryptionKey;
import com.tiger.cores.exceptions.BusinessLogicException;
import com.tiger.cores.exceptions.ErrorCode;
import com.tiger.cores.exceptions.SecureLogicException;
import com.tiger.cores.services.impl.RedisService;
import com.tiger.cores.utils.JsonUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecureService {

    @Value("${app.security.is-secure-enable:false}")
    private Boolean isSecureEnable;

    @Value("${app.security.time-millisecond:3000}")
    private Long timeMillisecond;

    private final RedisService redisService;
    private SecureRandom secureRandom;

    @PostConstruct
    void init() {
        this.secureRandom = new SecureRandom();
    }

    public InitSecureResponse initSecureProcess(String data) {
        try {
            // Decode and create public key object
            byte[] keyBytes = Base64.getDecoder().decode(data);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(AlgorithmEnum.RSA.getValue());
            PublicKey publicKey = keyFactory.generatePublic(spec);

            // Generate AES key
            String transactionKey = UUID.randomUUID().toString();
            AESEncryptionKey aesEncryptionKey = genAESEncryptionKey();

            // Encrypt response with RSA public key
            InitSecureResponse response = InitSecureResponse.builder()
                    .iss(isSecureEnable)
                    .sgId(transactionKey)
                    .eak(aesEncryptionKey)
                    .build();

            // Save AES key to Redis
            redisService.put(
                    SecureConstants.SECURE_KEY + transactionKey, JsonUtil.castToString(response), timeMillisecond);

            // encrypt response
            if (isSecureEnable) {
                encryptRSABase64(response, publicKey);
            }

            // Return encrypted data
            return response;
        } catch (Exception e) {
            log.error("[initSecureProcess] error {}", e.getMessage(), e);
            throw new BusinessLogicException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    private void encryptRSABase64(InitSecureResponse dataObject, PublicKey publicKey) {
        try {
            AESEncryptionKey aesEncryptionKey = dataObject.getEak();
            String encryptKey = rsaEncrypt(aesEncryptionKey.getK(), publicKey);
            String encryptIv = rsaEncrypt(aesEncryptionKey.getI(), publicKey);
            aesEncryptionKey.setK(encryptKey);
            aesEncryptionKey.setI(encryptIv);
        } catch (Exception e) {
            log.error("[encryptRSABase64] error {}", e.getMessage(), e);
            throw new BusinessLogicException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public String rsaEncrypt(String data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(AlgorithmEnum.RSA.getValue());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            log.error("[rsaEncrypt] error {}", e.getMessage(), e);
            throw new SecureLogicException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    // Temporary storage
    public static final String AES_KEY = "NFNsJ2Iox08ZlfDt9KiGLer7cNNoMRnqmtquSCEE0D0=";
    public static final String IV = "3ccb508381494a44";
    /**
     * Used to encrypt string fields
     */
    public static String encrypt(String data) {
        try {
            byte[] aesKeyBytes = Base64.getDecoder().decode(AES_KEY);
            SecretKeySpec secretKeySpec = new SecretKeySpec(aesKeyBytes, AlgorithmEnum.AES.getValue());
            Cipher cipher = Cipher.getInstance(CypherEnum.AES_VALUE.getValue());
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
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AlgorithmEnum.AES.getValue());
            Cipher cipher = Cipher.getInstance(CypherEnum.AES_VALUE.getValue());
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
    public static String genAESKeyV1() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AlgorithmEnum.AES.getValue());
            keyGenerator.init(256);
            return Base64.getEncoder().encodeToString(keyGenerator.generateKey().getEncoded());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessLogicException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    private AESEncryptionKey genAESEncryptionKey() {
        return AESEncryptionKey.builder().k(genAESKeyV2()).i(generateRandomIv()).build();
    }

    private String genAESKeyV2() {
        byte[] secureRandomKeyBytes = new byte[32];
        this.secureRandom.nextBytes(secureRandomKeyBytes);
        return Base64.getEncoder().encodeToString(secureRandomKeyBytes);
    }

    private String generateRandomIv() {
        try {
            byte[] iv =
                    new byte[Cipher.getInstance(CypherEnum.AES_VALUE.getValue()).getBlockSize()];
            this.secureRandom.nextBytes(iv);
            return Base64.getEncoder().encodeToString(iv);
        } catch (Exception e) {
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
