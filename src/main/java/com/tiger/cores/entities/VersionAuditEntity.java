package com.tiger.cores.entities;

import java.util.UUID;

import jakarta.persistence.MappedSuperclass;

import org.springframework.data.annotation.Version;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
public class VersionAuditEntity extends SoftDelEntity {

    @Version
    @Builder.Default
    private UUID version = UUID.randomUUID();
}
