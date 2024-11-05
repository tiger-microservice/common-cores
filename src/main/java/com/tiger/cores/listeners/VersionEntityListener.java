package com.tiger.cores.listeners;

import java.util.UUID;

import jakarta.persistence.PreUpdate;

import com.tiger.cores.entities.VersionAuditEntity;

public class VersionEntityListener {

    @PreUpdate
    public void preUpdate(VersionAuditEntity entity) {
        entity.setVersion(UUID.randomUUID());
    }
}
