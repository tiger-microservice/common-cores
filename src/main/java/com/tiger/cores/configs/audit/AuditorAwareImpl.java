package com.tiger.cores.configs.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

import com.tiger.cores.utils.UserInfoUtil;

import lombok.NonNull;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public @NonNull Optional<String> getCurrentAuditor() {
        return Optional.of(
                UserInfoUtil.getAccountUser() == null
                        ? "system"
                        : UserInfoUtil.getAccountUser().getName());
    }
}
