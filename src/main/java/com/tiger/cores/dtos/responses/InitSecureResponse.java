package com.tiger.cores.dtos.responses;

import com.tiger.cores.encryptors.securities.impl.AESEncryptionKey;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitSecureResponse {

    // Secure Generated ID
    private String sgId;

    // Encrypted AES Key
    private AESEncryptionKey eak;

    // Flag is secure
    @Builder.Default
    private boolean iss = false;
}
