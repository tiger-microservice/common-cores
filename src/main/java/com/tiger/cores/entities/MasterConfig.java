package com.tiger.cores.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "master_config")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MasterConfig {

    @Id
    @Column(name = "`id`")
    Long id;

    @Column(name = "`config_name`")
    String configName;

    @Column(name = "`config_value`")
    String configValue;
}
