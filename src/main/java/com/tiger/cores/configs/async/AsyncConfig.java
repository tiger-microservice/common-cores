package com.tiger.cores.configs.async;

import java.util.Map;
import java.util.concurrent.Executor;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import lombok.RequiredArgsConstructor;

@EnableAsync
@Configuration
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {

    @Value("${spring.application.name:service-name}")
    private String appName;

    private final ThreadPoolProperties threadPoolProperties;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Số lượng luồng cốt lõi, mặc định là 5
        executor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        // Số lượng luồng tối đa, mặc định là 10
        executor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        // Độ dài tối đa của hàng đợi, thường cần thiết lập giá trị đủ lớn
        executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix(appName + "-");
        // Thời gian chờ tối đa của luồng trong bộ nhớ cache, mặc định là 60s
        executor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
        // Cho phép đóng luồng hết thời gian chờ
        executor.setAllowCoreThreadTimeOut(threadPoolProperties.getAllowCoreThreadTimeOut());
        executor.initialize();
        executor.setTaskDecorator(new ContextCopyingDecorator());
        return executor;
    }

    //    @Primary
    //    @Bean(name = "asyncExecutor")
    //    public Executor asyncExecutor() {
    //        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    //        executor.setCorePoolSize(5);
    //        executor.setMaxPoolSize(15);
    //        executor.setQueueCapacity(1000);
    //        executor.setWaitForTasksToCompleteOnShutdown(true);
    //        executor.setThreadNamePrefix(appName + "-");
    //        executor.initialize();
    //        executor.setTaskDecorator(new ContextCopyingDecorator());
    //        return executor;
    //    }

    private static class ContextCopyingDecorator implements TaskDecorator {
        @NonNull
        @Override
        public Runnable decorate(@NonNull Runnable runnable) {
            // store context in variables which will be bound to the executor thread
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            SecurityContext securityContext = SecurityContextHolder.getContext();
            Map<String, String> mdcContextMap = MDC.getCopyOfContextMap();
            return () -> { // code runs inside executor thread and binds context
                try {
                    if (requestAttributes != null) {
                        RequestContextHolder.setRequestAttributes(requestAttributes);
                    }
                    if (securityContext != null) {
                        SecurityContextHolder.setContext(securityContext);
                    }
                    if (mdcContextMap != null) {
                        MDC.setContextMap(mdcContextMap);
                    }
                    runnable.run();
                } finally {
                    MDC.clear();
                    RequestContextHolder.resetRequestAttributes();
                    SecurityContextHolder.clearContext();
                }
            };
        }
    }
}
