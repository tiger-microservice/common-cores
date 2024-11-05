package com.tiger.cores.repositories.impl;

import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.tiger.cores.entities.VersionAuditEntity;
import com.tiger.cores.repositories.GenericVersionRepository;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class BaseVersionRepositoryImpl<T extends VersionAuditEntity> implements GenericVersionRepository<T> {

    @PersistenceContext
    protected EntityManager entityManager;

    @Override
    public Optional<T> findById(Object id, Class<T> entityClass) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }
}
