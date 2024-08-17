package com.tiger.cores.dtos.responses;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitSecureResponse {

    // Secure Generated ID
    private String sgId;

    // Encrypted AES Key
    private String eak;

    // Flag is secure
    private boolean iss;
}
