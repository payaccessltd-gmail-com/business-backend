package com.jamub.payaccess.api.config;

import com.codahale.metrics.MetricRegistry;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
public class JPAConfig {

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${spring.datasource.url}")
    private String jdbcUrl;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.hikari.maximumPoolSize}")
    private int maximumPoolSize;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Bean
    @Primary
    @Autowired
    public DataSource dataSource() throws Exception{
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(driverClassName);
        hikariDataSource.setJdbcUrl(jdbcUrl);
        hikariDataSource.setUsername(username);
        hikariDataSource.setPassword(password);
        hikariDataSource.setMaximumPoolSize(maximumPoolSize);
        hikariDataSource.setMaxLifetime(40*1000L);
//        hikariDataSource.setMetricRegistry(metricRegistry);

        return hikariDataSource;
    }

    @Bean
    public ApplicationRunner runner(DataSource dataSource) {
        return args -> {
            logger.info("dataSource: {}", dataSource);
            Connection connection = dataSource.getConnection();
//            logger.info("connection: {}", connection);
        };
    }
}
