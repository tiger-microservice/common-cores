package com.tiger.cores.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
@SQLRestriction("is_deleted <> true")
public class SoftDelEntity extends AuditEntity {

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;
}
