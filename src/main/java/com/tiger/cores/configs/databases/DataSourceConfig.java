package com.tiger.cores.configs.databases;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;

// same db, different schema
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.tenant.config.enable", havingValue = "true")
public class DataSourceConfig {

    private final TenantProperties tenantProperties;

    @Bean(name = "routingDataSource")
    @ConfigurationProperties(prefix = "app.tenant.tenants")
    public DataSource routingDataSource() {
        File[] files = Paths.get("tenants").toFile().listFiles();
        Map<Object, Object> resolvedDataSources = new HashMap<>();

        for (File propertyFile : files) {
            Properties tenantProperties = new Properties();
            try {
                tenantProperties.load(new FileInputStream(propertyFile));
                String tenantId = tenantProperties.getProperty("name");

                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(tenantProperties.getProperty("datasource.url"));
                config.setUsername(tenantProperties.getProperty("datasource.username"));
                config.setPassword(tenantProperties.getProperty("datasource.password"));
                config.setDriverClassName(tenantProperties.getProperty("datasource.driver-class-name"));
                config.setMaximumPoolSize(
                        Integer.parseInt(tenantProperties.getProperty("datasource.hikari.maximum-pool-size")));

                config.setMinimumIdle(Integer.parseInt(tenantProperties.getProperty("datasource.hikari.minimum-idle")));
                config.setIdleTimeout(Long.parseLong(tenantProperties.getProperty("datasource.hikari.idle-timeout")));
                config.setMaxLifetime(Long.parseLong(tenantProperties.getProperty("datasource.hikari.max-lifetime")));
                config.setConnectionTimeout(
                        Long.parseLong(tenantProperties.getProperty("datasource.hikari.connection-timeout")));
                resolvedDataSources.put(tenantId, new HikariDataSource(config));
            } catch (IOException exp) {
                throw new RuntimeException("Problem in tenant datasource:" + exp);
            }
        }
        AbstractRoutingDataSource routingDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return TenantContext.getCurrentTenant();
            }
        };
        routingDataSource.setDefaultTargetDataSource(resolvedDataSources.get(tenantProperties.getDefaultTenant()));
        routingDataSource.setTargetDataSources(resolvedDataSources);
        return routingDataSource;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            @Qualifier("routingDataSource") DataSource routingDataSource, Environment env) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(routingDataSource);
        factory.setPackagesToScan(tenantProperties.getPackageToScans());
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJpaPropertyMap(hibernateProperties(env));
        return factory;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory) {
        return new JpaTransactionManager(primaryEntityManagerFactory.getObject());
    }

    private Map<String, Object> hibernateProperties(Environment env) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.properties.hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
        properties.put("hibernate.show_sql", env.getProperty("spring.jpa.properties.hibernate.show_sql"));
        return properties;
    }
}
