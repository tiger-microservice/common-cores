package com.tiger.cores.aops.annotations;

import java.lang.annotation.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tiger.cores.constants.enums.VersionControlType;

/**
 * Annotation for controlling concurrent access to database records using versioning.
 * <p>
 * {@code VersionControl} is used to manage concurrent access to a specific record in the database,
 * ensuring data consistency by validating record versions. This annotation can be applied to methods
 * that retrieve or update a record to enforce concurrency control. When multiple users attempt to
 * access or modify the same record concurrently, version control helps to ensure that only one action
 * succeeds, while others may need to refresh their data to retrieve the latest version before retrying.
 * </p>
 *
 * <ul>
 * <li><b>objectIdKey</b>: Specifies the key or expression used to identify the unique ID of the record.</li>
 * <li><b>objectVersionKey</b>: Specifies the key or expression for the current version of the record.</li>
 * <li><b>repositoryClass</b>: Defines the {@link JpaRepository} class that manages the entity's data in the
 * database. This repository is used to handle database operations for the specified record.</li>
 * <li><b>type</b>: Defines the {@link VersionControlType} action for the annotation. Possible values are:
 * <ul>
 *   <li>{@code GET} - Used when retrieving the record.</li>
 *   <li>{@code UPDATE} - Used when updating the record.</li>
 *   <li>{@code UPDATE_GET} - Used when updating and GET version of record.</li>
 * </ul>
 * </li>
 * </ul>
 *
 * <p>
 * Example Usage:
 * </p>
 * <p><b>For retrieving data:</b></p>
 * <pre>
 * {@code
 * @GetMapping("/{id}")
 * @VersionControl(
 *         name = "XXX",
 *         objectIdKey = "#id",
 *         objectVersionKey = "#result.version")
 * public ResponseEntity<Record> getRecord(@PathVariable Long id) {
 *     // Method logic for fetching the record
 * }
 * }
 * </pre>
 *
 * <p><b>For updating data:</b></p>
 * <pre>
 * {@code
 * @PutMapping("/{id}")
 * @VersionControl(
 *         name = "XXX",
 *         id = "#id",
 *         repositoryClass = XXXXRepository.class,
 *         type = VersionControlType.UPDATE)
 * public ResponseEntity<Void> updateRecord(@PathVariable Long id, @RequestBody RecordUpdateRequest request) {
 *     // Method logic for updating the record
 * }
 * }
 * </pre>
 *
 * <p><b>Example for updating and get data:</b></p>
 * <pre>
 * {@code
 * @PutMapping("/{id}")
 * @VersionControl(
 *         name = "XXX",
 *         version = "#result.version"
 *         repositoryClass = XXXRepository.class,
 *         type = VersionControlType.UPDATE_GET)
 * public ResponseEntity<Void> updateRecord(@PathVariable Long id, @RequestBody RecordUpdateRequest request) {
 *     // Method logic for updating the record
 * }
 * }
 * </pre>
 *
 * @see VersionControlType
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface VersionControl {

    String name();

    /**
     * Specifies the key or expression for the unique identifier of the record.
     * This ID is used to identify the specific record that is being accessed or updated.
     *
     * @return the expression for the record ID
     */
    String id() default "";

    /**
     * Specifies the key or expression for the current version of the record.
     * This version is used to validate concurrency when performing updates.
     *
     * @return the expression for the record version
     */
    String version() default "";

    /**
     * Defines the repository class managing the entity's data.
     * This repository handles the necessary database operations for the entity.
     * It should implement {@link JpaRepository}.
     *
     * @return the repository class associated with the entity
     */
    Class<? extends JpaRepository> repositoryClass();

    /**
     * Defines the type of version control action to apply, either retrieving or updating the record.
     * The {@link VersionControlType} enum specifies the available actions:
     * <ul>
     *   <li>{@code GET} - Used when retrieving the record.</li>
     *   <li>{@code UPDATE} - Used when updating the record.</li>
     *   <li>{@code UPDATE_GET} - Used when updating and GET version of record.</li>
     * </ul>
     *
     * @return the version control action type
     */
    VersionControlType type() default VersionControlType.GET;

    /**
     * Defines the Time-To-Live (TTL) duration in milliseconds for tracking the version information of the record.
     * This helps to maintain version control for a specific time period, after which the version information
     * may expire. Default value is 8 hours (28,800,000 ms).
     *
     * @return the TTL for version tracking, in milliseconds
     */
    long versionTrackingTtl() default 28800000L;

    /**
     * Defines the Time-To-Live (TTL) duration in milliseconds for locking access to a record during updates.
     * This temporary lock prevents multiple users from simultaneously updating the same record.
     * Default value is 30 seconds (30,000 ms).
     *
     * @return the TTL for locking the record, in milliseconds
     */
    long timeLockingTtl() default 30000L;
}
