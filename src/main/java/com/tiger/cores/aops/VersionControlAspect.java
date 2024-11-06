package com.tiger.cores.aops;

import java.util.Optional;

import org.apache.commons.text.WordUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.tiger.cores.aops.annotations.VersionControl;
import com.tiger.cores.constants.enums.VersionControlType;
import com.tiger.cores.entities.VersionAuditEntity;
import com.tiger.cores.exceptions.BusinessLogicException;
import com.tiger.cores.exceptions.ErrorCode;
import com.tiger.cores.exceptions.StaleDataException;
import com.tiger.cores.services.CacheService;
import com.tiger.cores.services.VersionTrackingService;
import com.tiger.cores.utils.UserInfoUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class VersionControlAspect extends AbstractAspect {

    private final CacheService cacheService;
    private final ApplicationContext applicationContext;
    private final VersionTrackingService versionTrackingService;

    @Around("@annotation(versionControl)")
    public Object handleVersionControl(ProceedingJoinPoint joinPoint, VersionControl versionControl) throws Throwable {
        // get username
        String username = getCurrentUsername();
        log.info("[VersionControl] username: {}", username);

        // get config objectId from version control
        Object objectId = getValueByExpressionFromRequest(joinPoint, versionControl.objectIdKey());
        log.info("[VersionControl] objectId: {}", objectId);

        // check if is GET
        if (isGetRequest(versionControl.type())) {
            Object result = joinPoint.proceed();

            // get config objectVersion from version control
            var objectVersion = getValueByExpressionFromResponse(result, versionControl.objectVersionKey());
            log.info("[VersionControl] objectVersion: {}", objectVersion);

            // 8 hours
            long ttl = 8 * 60 * 60 * 1000;

            // cache object into redis with key=username::id, value=version
            versionTrackingService.trackVersion(username, objectId.toString(), objectVersion.toString(), ttl);

            return result;
        }

        // check if is UPDATE
        String lockKey = generateKeyObject(username, objectId.toString());

        // check and lock in 30 seconds
        if (cacheService.lock(lockKey, objectId.toString(), 30 * 1000)) {
            try {
                // if lock data is true
                // get version of user
                String versionOfUser = this.versionTrackingService.getUserVersion(username, objectId.toString());
                log.info("[VersionControl] versionOfUser: {}", versionOfUser);

                // get version of record
                var recordData = this.getCurrentEntity(objectId, versionControl);
                String versionOfRecord = recordData.getVersion().toString();
                log.info("[VersionControl] versionOfRecord: {}", versionOfRecord);

                // check version record
                // if record version different with record user -> throw exception
                if (Boolean.FALSE.equals(versionOfUser.equals(versionOfRecord))) {
                    throw throwStaleDataException();
                }

                return joinPoint.proceed();
            } finally {
                // release lock
                this.cacheService.releaseLock(lockKey);
            }
        }

        // if lock data is fail -> throw exception because record used other user, logging user updating this data
        throw throwStaleDataException();
    }

    private StaleDataException throwStaleDataException() {
        return new StaleDataException(ErrorCode.CONCURRENT_REQUEST_ERROR);
    }

    private VersionAuditEntity getCurrentEntity(Object entityId, VersionControl versionControl) {
        // find bean repository by class name
        var repository = (JpaRepository) applicationContext.getBean(getRepositoryName(versionControl.repositoryClass()));
        Optional<VersionAuditEntity> recordData = repository.findById(entityId);
        return recordData.orElseThrow(() -> new BusinessLogicException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    private String getRepositoryName(Class clazz) {
        if (clazz == null) throw new BusinessLogicException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        return WordUtils.uncapitalize(clazz.getSimpleName());
    }

    private String generateKeyObject(String username, String objectId) {
        return username + "::" + objectId;
    }

    private boolean isGetRequest(VersionControlType versionControlType) {
        return VersionControlType.GET.equals(versionControlType);
    }

    private String getCurrentUsername() {
        return UserInfoUtil.getUserInfo().getEmail();
    }
}
