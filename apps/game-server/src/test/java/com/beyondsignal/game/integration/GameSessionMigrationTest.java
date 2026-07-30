package com.beyondsignal.game.integration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers(disabledWithoutDocker = true)
class GameSessionMigrationTest {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
        new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("beyond_signal")
            .withUsername("beyond_signal")
            .withPassword("change-me");

    @BeforeAll
    static void migrate() {
        Flyway.configure()
            .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
            .locations("classpath:db/migration")
            .load()
            .migrate();
    }

    @Test
    void createsSessionPersistenceTables() throws SQLException {
        try (Connection connection = POSTGRES.createConnection("")) {
            assertThat(tableExists(connection, "game_sessions")).isTrue();
            assertThat(tableExists(connection, "players")).isTrue();
            assertThat(tableExists(connection, "station_assignments")).isTrue();
        }
    }

    @Test
    void rejectsPlayerForUnknownSession() throws SQLException {
        try (Connection connection = POSTGRES.createConnection("")) {
            UUID playerId = UUID.randomUUID();
            UUID sessionId = UUID.randomUUID();

            assertThatThrownBy(() -> execute(connection, """
                INSERT INTO players (id, session_id, display_name, connected, joined_at, is_host)
                VALUES ('%s', '%s', 'Science Officer', TRUE, NOW(), FALSE)
                """.formatted(playerId, sessionId)))
                .isInstanceOf(SQLException.class);
        }
    }

    @Test
    void enforcesOneNonObserverStationPerPlayer() throws SQLException {
        try (Connection connection = POSTGRES.createConnection("")) {
            connection.setAutoCommit(false);
            UUID sessionId = UUID.randomUUID();
            UUID hostId = UUID.randomUUID();

            execute(connection, """
                INSERT INTO game_sessions (id, session_name, ship_name, status, host_player_id, created_at)
                VALUES ('%s', 'First Contact', 'BTS Horizon', 'CREATED', '%s', NOW())
                """.formatted(sessionId, hostId));
            execute(connection, """
                INSERT INTO players (id, session_id, display_name, connected, joined_at, is_host)
                VALUES ('%s', '%s', 'Captain Vale', TRUE, NOW(), TRUE)
                """.formatted(hostId, sessionId));
            execute(connection, """
                INSERT INTO station_assignments (session_id, station, player_id, assigned_at)
                VALUES ('%s', 'CAPTAIN', '%s', NOW())
                """.formatted(sessionId, hostId));

            assertThatThrownBy(() -> execute(connection, """
                INSERT INTO station_assignments (session_id, station, player_id, assigned_at)
                VALUES ('%s', 'SCIENCE', '%s', NOW())
                """.formatted(sessionId, hostId)))
                .isInstanceOf(SQLException.class);

            connection.rollback();
        }
    }

    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        try (ResultSet tables = connection.getMetaData().getTables(null, "public", tableName, new String[]{"TABLE"})) {
            return tables.next();
        }
    }

    private static void execute(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
