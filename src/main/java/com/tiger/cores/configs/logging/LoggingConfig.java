package com.tiger.cores.configs.logging;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        value = "app.logging.config.enable",
        havingValue = "true", // Nếu giá trị app.redisson.config  = true thì Bean mới được khởi tạo
        matchIfMissing = true) // matchIFMissing là giá trị mặc định nếu không tìm thấy property app.redisson.config
public class LoggingConfig {

    private static final String PACKAGE_NAME = "com.tiger.cores.configs.logging.LoggingConfig.";
    private static final String WITHIN_ANNOTATION_REPOSITORY = "within(@org.springframework.stereotype.Repository *)";
    private static final String WITHIN_ANNOTATION_SERVICE = "within(@org.springframework.stereotype.Service *)";
    private static final String WITHIN_ANNOTATION_REST_CONTROLLER =
            "within(@org.springframework.web.bind.annotation.RestController *)";
    public static final String BASE_BEANS_POINTCUT = PACKAGE_NAME + "springBeanPointcut()";
    public static final String REST_CONTROLLER_BEANS_POINTCUT = PACKAGE_NAME + "springBeanControllerPointcut()";

    @Pointcut("within(com.tiger..*) && execution(* com.tiger.*.services.*.*(..)))")
    public void serviceLayerPointcut() {}

    // https://stackoverflow.com/questions/58392441/configure-pointcut-for-spring-jpa-repository-methods
    @Pointcut("within(com.tiger..*) && execution(* org.springframework.data.jpa.repository.JpaRepository+.*(..))))")
    public void repositoryLayerPointcut() {}

    @Pointcut("within(com.tiger..*) && execution(* com.tiger.*.controllers.*.*.*.*(..)))")
    public void controllerLayerPointcut() {}

    @Pointcut(WITHIN_ANNOTATION_REPOSITORY + " || " + WITHIN_ANNOTATION_SERVICE + " || "
            + WITHIN_ANNOTATION_REST_CONTROLLER)
    public void springBeanPointcut() {}

    @Pointcut(WITHIN_ANNOTATION_REST_CONTROLLER)
    public void springBeanControllerPointcut() {}
}
