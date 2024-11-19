package com.tiger.cores.configs.async;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.thread-pool")
public class ThreadPoolProperties {
    /**
     * Số lượng luồng core
     */
    private Integer corePoolSize = 10;

    /**
     * Số lượng luồng tối đa
     */
    private Integer maxPoolSize = 50;

    /**
     * Độ dài tối đa của hàng đợi
     */
    private Integer queueCapacity = Integer.MAX_VALUE;

    /**
     * Cho phép đóng luồng hết thời gian chờ
     */
    private Boolean allowCoreThreadTimeOut = false;

    /**
     * Keep Alive
     */
    private Integer keepAliveSeconds = 60;
}
