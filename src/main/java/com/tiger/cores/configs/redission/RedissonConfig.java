package com.tiger.cores.configs.redission;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(
        value = "app.redisson.config.enable",
        havingValue = "true", // Nếu giá trị app.redisson.config  = true thì Bean mới được khởi tạo
        matchIfMissing = true) // matchIFMissing là giá trị mặc định nếu không tìm thấy property app.redisson.config
public class RedissonConfig {

    final RedissonProperties redissonProperties;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(redissonProperties.getAddress())
                .setPassword(redissonProperties.getPassword())
                .setConnectionPoolSize(redissonProperties.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(redissonProperties.getIdleConnectionTimeout())
                .setConnectTimeout(redissonProperties.getTimeout())
                .setPingConnectionInterval(redissonProperties.getPingTimeout());
        return Redisson.create(config);
    }
}
