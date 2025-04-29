package com.rstep1.user_service;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractIntegrationTest {
    
    @Container
    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> postgresqlContainer = 
        new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("userservicetestdb")
            .withUsername("username")
            .withPassword("password");
            
    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }
}
