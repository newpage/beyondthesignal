package com.beyondsignal.game.persistence.jooq;

import com.beyondsignal.game.domain.BridgeStation;
import com.beyondsignal.game.domain.GameSession;
import com.beyondsignal.game.domain.Player;
import com.beyondsignal.game.domain.SessionStatus;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
class JooqGameSessionRepositoryTest {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
        new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("beyond_signal")
            .withUsername("beyond_signal")
            .withPassword("change-me");

    private Connection connection;
    private DSLContext dsl;
    private JooqGameSessionRepository repository;

    @BeforeAll
    static void migrate() {
        Flyway.configure()
            .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
            .locations("classpath:db/migration")
            .load()
            .migrate();
    }

    @BeforeEach
    void setUp() throws Exception {
        connection = DriverManager.getConnection(
            POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
        dsl = DSL.using(connection, SQLDialect.POSTGRES);
        dsl.deleteFrom(GameSessionTables.GAME_SESSIONS).execute();
        repository = new JooqGameSessionRepository(dsl);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void savesAndRestoresCompleteAggregate() {
        Instant createdAt = Instant.parse("2026-07-29T20:00:00Z");
        GameSession session = GameSession.create("First Contact", "BTS Horizon", "Captain Vale", createdAt);
        Player scienceOfficer = Player.crewMember("Dr. Chen", createdAt.plusSeconds(10));
        session.addPlayer(scienceOfficer);
        session.assignStation(BridgeStation.CAPTAIN, session.hostPlayerId(), createdAt.plusSeconds(20));
        session.assignStation(BridgeStation.SCIENCE, scienceOfficer.id(), createdAt.plusSeconds(30));
        session.transitionTo(SessionStatus.WAITING_FOR_PLAYERS, createdAt.plusSeconds(40));

        repository.save(session);

        GameSession restored = repository.findById(session.id()).orElseThrow();
        assertThat(restored.id()).isEqualTo(session.id());
        assertThat(restored.sessionName()).isEqualTo("First Contact");
        assertThat(restored.shipName()).isEqualTo("BTS Horizon");
        assertThat(restored.status()).isEqualTo(SessionStatus.WAITING_FOR_PLAYERS);
        assertThat(restored.players()).extracting(Player::displayName)
            .containsExactly("Captain Vale", "Dr. Chen");
        assertThat(restored.assignments()).containsKeys(BridgeStation.CAPTAIN, BridgeStation.SCIENCE);
    }

    @Test
    void synchronizesRemovedPlayersAndAssignments() {
        Instant now = Instant.parse("2026-07-29T20:00:00Z");
        GameSession session = GameSession.create("Signal Lost", "BTS Meridian", "Captain Vale", now);
        Player tactical = Player.crewMember("Lt. Reyes", now.plusSeconds(1));
        session.addPlayer(tactical);
        session.assignStation(BridgeStation.TACTICAL, tactical.id(), now.plusSeconds(2));
        repository.save(session);

        session.removePlayer(tactical.id());
        repository.save(session);

        GameSession restored = repository.findById(session.id()).orElseThrow();
        assertThat(restored.players()).hasSize(1);
        assertThat(restored.assignments()).doesNotContainKey(BridgeStation.TACTICAL);
    }

    @Test
    void listsChecksAndDeletesSessions() {
        Instant now = Instant.parse("2026-07-29T20:00:00Z");
        GameSession first = GameSession.create("Alpha", "BTS Alpha", "Captain A", now);
        GameSession second = GameSession.create("Beta", "BTS Beta", "Captain B", now.plusSeconds(1));
        repository.save(first);
        repository.save(second);

        assertThat(repository.existsById(first.id())).isTrue();
        assertThat(repository.findAll()).extracting(GameSession::id)
            .containsExactly(second.id(), first.id());

        repository.deleteById(first.id());
        assertThat(repository.existsById(first.id())).isFalse();
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
    }
}
