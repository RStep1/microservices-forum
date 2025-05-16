package com.rstep1.user_service.dbmigration;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

@SpringBootTest
@Testcontainers
public class UserMigrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("migrationtestdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private DataSource dataSource;

    private Flyway flyway;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }
    
    @AfterEach
    void tearDown() {
        flyway.clean();
    }

    private void configureAndMigrateToVersion(String version) {
        flyway = Flyway.configure()
                .dataSource(dataSource)
                .baselineOnMigrate(true)
                .cleanDisabled(false)
                .target(MigrationVersion.fromVersion(version))
                .load();
        flyway.migrate();
    }

    @Test
    void givenInitialMigration_whenCheckingTables_thenUsersTableExists() throws SQLException {
        configureAndMigrateToVersion("1");

        try (Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(
                "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'users')"
            );
            rs.next();
            assertTrue(rs.getBoolean(1));
        }
    }

    @Test
    void givenVersion2Migration_whenCheckingColumns_thenBioColumnExists() throws SQLException {
        configureAndMigrateToVersion("2");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(
                "SELECT column_name FROM information_schema.columns " +
                "WHERE table_name = 'users' AND column_name = 'bio'"
            );
            assertTrue(rs.next());
        }
    }

    @Test
    void givenVersion3Migration_whenQueryingTestUser_thenReturnsCorrectBio() throws SQLException {
        configureAndMigrateToVersion("3");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(
                "SELECT bio FROM users WHERE username = 'test_user'"
            );
            assertTrue(rs.next());
            assertEquals("Test bio", rs.getString("bio"));
        }
    }
    
    @Test
    void givenVersion3Migration_whenRollingBackToVersion1_thenBioColumnShouldNotExist() {
        configureAndMigrateToVersion("3");
        
        flyway = Flyway.configure()
            .dataSource(dataSource)
            .baselineOnMigrate(true)
            .cleanDisabled(false)
            .target(MigrationVersion.fromVersion("1"))
            .load();
        flyway.clean();
        flyway.migrate();

        assertThrows(SQLException.class, () -> {
            try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement()) {
                stmt.executeQuery("SELECT bio FROM users");
            }
        });
    }

    @Test
    void givenEmptyUsersTable_whenCreatingNewUser_thenUserIsPersisted() throws SQLException {
        configureAndMigrateToVersion("1");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            int affectedRows = stmt.executeUpdate(
                "INSERT INTO users (username, email, password) " +
                "VALUES ('new_user', 'new@example.com', 'pass123')"
            );
            assertEquals(1, affectedRows);

            ResultSet rs = stmt.executeQuery(
                "SELECT * FROM users WHERE username = 'new_user'"
            );
            assertTrue(rs.next());
            assertEquals("new@example.com", rs.getString("email"));
        }
    }

    @Test
    void givenVersion3Migration_whenQueryingAdminUser_thenReturnsCorrectData() throws SQLException {
        configureAndMigrateToVersion("3");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(
                "SELECT * FROM users WHERE username = 'admin'"
            );
            assertTrue(rs.next());
            assertEquals("admin@example.com", rs.getString("email"));
            assertEquals("Admin bio", rs.getString("bio"));
        }
    }

    @Test
    void givenExistingUser_whenUpdatingBio_thenChangesArePersisted() throws SQLException {
        configureAndMigrateToVersion("3");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            int affectedRows = stmt.executeUpdate(
                "UPDATE users SET bio = 'Updated bio' WHERE username = 'test_user'"
            );
            assertEquals(1, affectedRows);

            ResultSet rs = stmt.executeQuery(
                "SELECT bio FROM users WHERE username = 'test_user'"
            );
            assertTrue(rs.next());
            assertEquals("Updated bio", rs.getString("bio"));
        }
    }

    @Test
    void givenExistingUser_whenDeletingUser_thenUserIsRemoved() throws SQLException {
        configureAndMigrateToVersion("3");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(
                "SELECT 1 FROM users WHERE username = 'admin'"
            );
            assertTrue(rs.next());

            int affectedRows = stmt.executeUpdate(
                "DELETE FROM users WHERE username = 'admin'"
            );
            assertEquals(1, affectedRows);

            rs = stmt.executeQuery(
                "SELECT 1 FROM users WHERE username = 'admin'"
            );
            assertFalse(rs.next());
        }
    }

    @Test
    void givenAllMigrationsApplied_whenCheckingTableStructure_thenContainsAllExpectedColumns() throws SQLException {
        configureAndMigrateToVersion("3");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(
                "SELECT column_name, data_type FROM information_schema.columns " +
                "WHERE table_name = 'users' ORDER BY column_name"
            );

            List<String> columns = new ArrayList<>();
            while (rs.next()) {
                columns.add(rs.getString("column_name") + ":" + rs.getString("data_type"));
            }

            assertAll(
                () -> assertTrue(columns.contains("bio:character varying")),
                () -> assertTrue(columns.contains("email:character varying")),
                () -> assertTrue(columns.contains("id:integer")),
                () -> assertTrue(columns.contains("password:character varying")),
                () -> assertTrue(columns.contains("username:character varying"))
            );
        }
    }

    @Test
    void givenUniqueConstraints_whenInsertingDuplicateData_thenThrowsException() throws SQLException {
        configureAndMigrateToVersion("3");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            assertThrows(SQLException.class, () -> {
                stmt.executeUpdate(
                    "INSERT INTO users (username, email, password, bio) " +
                    "VALUES ('test_user', 'duplicate@example.com', 'pass123', 'bio')"
                );
            });

            assertThrows(SQLException.class, () -> {
                stmt.executeUpdate(
                    "INSERT INTO users (username, email, password, bio) " +
                    "VALUES ('duplicate', 'test@example.com', 'pass123', 'some bio')"
                );
            });
        }
    }
}
