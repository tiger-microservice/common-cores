package com.tiger.cores.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class UpdatedAuditEntity extends CreatedAuditEntity {

    @Column(name = "updated_date")
    @LastModifiedDate
    private LocalDateTime updatedDate;

    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;
}
