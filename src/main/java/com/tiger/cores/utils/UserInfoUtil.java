package com.tiger.cores.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tiger.cores.constants.AppConstants;
import com.tiger.cores.constants.enums.Domain;
import com.tiger.cores.dtos.UserPayloadDto;

public final class UserInfoUtil {

    public static Authentication getAccountUser() {
        var context = SecurityContextHolder.getContext();

        return context.getAuthentication();
    }

    public static Domain getClientSite() {
        String clientSite = SecurityUtils.getClaim(AppConstants.JwtKey.DOMAIN);
        return Domain.valueOf(clientSite);
    }

    public static UserPayloadDto getUserInfo() {
        String dataJson = SecurityUtils.getClaim(AppConstants.JwtKey.DATA);
        return JsonUtil.castToObject(dataJson, UserPayloadDto.class);
    }
}
