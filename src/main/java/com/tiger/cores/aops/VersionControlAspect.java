package com.tiger.cores.aops;

import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.tiger.cores.aops.annotations.VersionControl;
import com.tiger.cores.constants.enums.VersionControlType;
import com.tiger.cores.entities.VersionAuditEntity;
import com.tiger.cores.exceptions.BusinessLogicException;
import com.tiger.cores.exceptions.ErrorCode;
import com.tiger.cores.exceptions.StaleDataException;
import com.tiger.cores.repositories.GenericVersionRepository;
import com.tiger.cores.services.VersionTrackingService;
import com.tiger.cores.utils.UserInfoUtil;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class VersionControlAspect extends AbstractAspect {

    private final ApplicationContext applicationContext;
    private final VersionTrackingService versionTrackingService;

    @Around("@annotation(versionControl)")
    public Object handleVersionControl(ProceedingJoinPoint joinPoint, VersionControl versionControl) throws Throwable {
        String username = getCurrentUsername();
        String entityType = versionControl.entityType();

        // Nếu là GET request
        if (isGetRequest(versionControl)) {
            Object result = joinPoint.proceed();
            if (result instanceof VersionAuditEntity) {
                VersionAuditEntity entity = (VersionAuditEntity) result;
                versionTrackingService.trackVersion(username, entityType, versionControl.key(), entity.getVersion());
            }
            return result;
        }

        // Nếu là UPDATE request
        else {
            // Lấy entity ID từ request
            String entityId = parserKey(joinPoint, versionControl.key());
            String userVersion = versionTrackingService.getUserVersion(username, entityType, entityId);

            // Kiểm tra version
            VersionAuditEntity currentEntity = getCurrentEntity(entityId, versionControl); // implement theo repository
            if (!currentEntity.getVersion().equals(userVersion)) {
                throw new StaleDataException("Data has been modified. Please refresh.");
            }

            return joinPoint.proceed();
        }
    }

    private VersionAuditEntity getCurrentEntity(Object entityId, VersionControl versionControl) {
        String repositoryBeanName = versionControl.entityClass().getSimpleName() + "Repository";
        GenericVersionRepository repository = (GenericVersionRepository)
                applicationContext.getBean(repositoryBeanName);
        Optional<VersionAuditEntity> byId = repository.findById(entityId, versionControl.entityClass());
        return byId.orElseThrow(() -> new BusinessLogicException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    private boolean isGetRequest(VersionControl versionControl) {
        return VersionControlType.GET.equals(versionControl.entityType());
    }

    private String getCurrentUsername() {
        return UserInfoUtil.getUserInfo().getEmail();
    }
}
