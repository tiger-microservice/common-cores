package com.tiger.cores.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiger.cores.entities.MasterConfig;

@Repository
public interface ConfigRepository extends JpaRepository<MasterConfig, Long> {

    MasterConfig findByConfigName(String configName);
}
