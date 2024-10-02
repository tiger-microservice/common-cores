package com.tiger.cores.dtos.responses;

import com.tiger.cores.encryptors.securities.impl.AESEncryptionKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
