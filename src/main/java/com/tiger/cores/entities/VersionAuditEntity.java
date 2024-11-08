package com.tiger.cores.entities;

import java.util.UUID;

import com.tiger.cores.listeners.VersionEntityListener;
import jakarta.persistence.EntityListeners;
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
@EntityListeners(VersionEntityListener.class)
public class VersionAuditEntity extends SoftDelEntity {

    @Version
    @Builder.Default
    private UUID version = UUID.randomUUID();
}
