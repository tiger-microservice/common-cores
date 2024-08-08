package com.tiger.cores.configs.async;

import java.util.Map;
import java.util.concurrent.Executor;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@EnableAsync
@Configuration
public class AsyncConfig {

    @Primary
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(15);
        executor.setQueueCapacity(1000);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("authorization-service-");
        executor.initialize();
        executor.setTaskDecorator(new ContextCopyingDecorator());
        return executor;
    }

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
