package com.tiger.cores.repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.tiger.cores.entities.VersionAuditEntity;

public interface GenericVersionRepository<T extends VersionAuditEntity> {
    Optional<T> findById(Object id, Class<T> entityClass);
}
