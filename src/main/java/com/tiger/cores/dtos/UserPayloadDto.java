package com.tiger.cores.dtos;

import java.util.UUID;

import com.tiger.cores.constants.enums.AccountState;
import com.tiger.cores.constants.enums.MfaType;
import com.tiger.cores.constants.enums.OnlineStatus;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPayloadDto {

    UUID id;
    String slug;
    String username;
    String email;
    String phone;
    String avatar;
    Boolean mfa;
    AccountState accountState;
    MfaType mfaType;
    OnlineStatus onlineStatus;
}
