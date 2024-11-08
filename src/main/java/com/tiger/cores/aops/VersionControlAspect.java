package com.tiger.cores.aops;

import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.tiger.cores.aops.annotations.VersionControl;
import com.tiger.cores.constants.AppConstants;
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

/**
 * Aspect for controlling concurrent access to a record using the {@code @VersionControl} annotation.
 * <p>
 * This aspect intercepts methods annotated with {@code @VersionControl} to ensure that concurrent
 * modifications to the same record are managed properly. When multiple users attempt to access or
 * update the same record concurrently, only one update will succeed, while others will receive a
 * failure response. Failed attempts require a record refresh to obtain the latest version before
 * retrying the update.
 * </p>
 *
 * <p>
 * The {@code VersionControlAspect} ensures consistency and prevents potential conflicts by enforcing
 * version-based control over record modifications and retrievals.
 * </p>
 *
 * <p>
 * Usage Examples:
 * </p>
 * <p><b>Example for retrieving data:</b></p>
 * <pre>
 * {@code
 * @GetMapping("/{id}")
 * @VersionControl(
 *         objectIdKey = "#id",
 *         objectVersionKey = "#result.version")
 * public ResponseEntity<Record> getRecord(@PathVariable Long id) {
 *     // Method logic for fetching the record
 * }
 * }
 * </pre>
 *
 * <p><b>Example for updating data:</b></p>
 * <pre>
 * {@code
 * @PutMapping("/{id}")
 * @VersionControl(
 *         objectIdKey = "#id",
 *         repositoryClass = XXXRepository.class,
 *         type = VersionControlType.UPDATE)
 * public ResponseEntity<Void> updateRecord(@PathVariable Long id, @RequestBody RecordUpdateRequest request) {
 *     // Method logic for updating the record
 * }
 * }
 * </pre>
 *
 * @see VersionControl
 */
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
        Object objectId = getValueByExpressionFromRequest(joinPoint, versionControl.id());
        log.info("[VersionControl] objectId: {}", objectId);

        // check if is GET
        if (isGetRequest(versionControl.type())) {
            return actionTypeIsGet(joinPoint, versionControl, username, objectId);
        }

        // check if is UPDATE
        String lockKey = generateKeyObject(username, objectId.toString());

        // check and lock in 30 seconds
        if (cacheService.lock(lockKey, objectId.toString(), versionControl.timeLockingTtl())) {
            try {
                // if lock data is true
                // get version of user
                // check user is system -> ignore check version
                if ("system".equals(username)) {
                    log.warn("[VersionControl] username {} ignore check version", username);
                } else {
                    actionTypeIsUpdate(versionControl, username, objectId);
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

    private void actionTypeIsUpdate(VersionControl versionControl, String username, Object objectId) {
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
    }

    private Object actionTypeIsGet(
            ProceedingJoinPoint joinPoint, VersionControl versionControl, String username, Object objectId)
            throws Throwable {
        Object result = joinPoint.proceed();

        // get config objectVersion from version control
        var objectVersion = getValueByExpressionFromResponse(result, versionControl.version());
        log.info("[VersionControl] objectVersion: {}", objectVersion);

        // cache object into redis with key=username:id, value=version
        versionTrackingService.trackVersion(
                username, objectId.toString(), objectVersion.toString(), versionControl.versionTrackingTtl());

        return result;
    }

    private StaleDataException throwStaleDataException() {
        return new StaleDataException(ErrorCode.CONCURRENT_REQUEST_ERROR);
    }

    @SuppressWarnings("unchecked")
    private <T, ID> JpaRepository<T, ID> getRepository(Class<?> repositoryClass) {
        return (JpaRepository<T, ID>) applicationContext.getBean(repositoryClass);
    }

    private VersionAuditEntity getCurrentEntity(Object entityId, VersionControl versionControl) {
        JpaRepository<VersionAuditEntity, Object> repository = getRepository(versionControl.repositoryClass());

        Optional<VersionAuditEntity> recordData = repository.findById(entityId);
        return recordData.orElseThrow(() -> new BusinessLogicException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    private String generateKeyObject(String username, String objectId) {
        return username + AppConstants.KEY_SEPARATOR + objectId;
    }

    private boolean isGetRequest(VersionControlType versionControlType) {
        return VersionControlType.GET.equals(versionControlType);
    }

    private String getCurrentUsername() {
        return UserInfoUtil.getUserInfo().getEmail();
    }
}
