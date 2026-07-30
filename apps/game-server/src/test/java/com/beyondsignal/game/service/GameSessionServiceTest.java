package com.beyondsignal.game.service;

import com.beyondsignal.game.domain.BridgeStation;
import com.beyondsignal.game.domain.GameSession;
import com.beyondsignal.game.domain.Player;
import com.beyondsignal.game.domain.SessionStatus;
import com.beyondsignal.game.persistence.GameSessionRepository;
import com.beyondsignal.game.service.command.AssignStationCommand;
import com.beyondsignal.game.service.command.CreateSessionCommand;
import com.beyondsignal.game.service.command.JoinSessionCommand;
import com.beyondsignal.game.service.command.StartSessionCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameSessionServiceTest {
    private static final Instant NOW = Instant.parse("2026-07-30T12:00:00Z");
    private InMemoryGameSessionRepository repository;
    private GameSessionService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryGameSessionRepository();
        service = new GameSessionService(repository, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void createsWaitingSessionWithHost() {
        GameSession session = service.createSession(new CreateSessionCommand("Alpha Shift", "BTS Horizon", "Robert"));
        assertThat(session.status()).isEqualTo(SessionStatus.WAITING_FOR_PLAYERS);
        assertThat(session.players()).singleElement().satisfies(host -> {
            assertThat(host.host()).isTrue();
            assertThat(host.displayName()).isEqualTo("Robert");
        });
        assertThat(repository.findById(session.id())).containsSame(session);
    }

    @Test
    void joinsPlayerAndPersistsAggregate() {
        GameSession session = createSession();
        Player joined = service.joinSession(new JoinSessionCommand(session.id(), "Tactical Officer"));
        assertThat(service.getSession(session.id()).players()).extracting(Player::id).contains(joined.id());
        assertThat(repository.saveCount).isEqualTo(2);
    }

    @Test
    void rejectsHostOnlyOperationFromCrewMember() {
        GameSession session = createSession();
        Player crew = service.joinSession(new JoinSessionCommand(session.id(), "Science Officer"));
        assertThatThrownBy(() -> service.assignStation(new AssignStationCommand(session.id(), crew.id(), crew.id(), BridgeStation.SCIENCE)))
            .isInstanceOf(SessionAuthorizationException.class)
            .hasMessageContaining("host");
    }

    @Test
    void hostAssignsStation() {
        GameSession session = createSession();
        Player crew = service.joinSession(new JoinSessionCommand(session.id(), "Helm Officer"));
        GameSession updated = service.assignStation(new AssignStationCommand(session.id(), session.hostPlayerId(), crew.id(), BridgeStation.HELM));
        assertThat(updated.assignments().get(BridgeStation.HELM).playerId()).isEqualTo(crew.id());
    }

    @Test
    void hostMarksReadyAndStartsSession() {
        GameSession session = createSession();
        service.markReady(session.id(), session.hostPlayerId());
        GameSession running = service.startSession(new StartSessionCommand(session.id(), session.hostPlayerId()));
        assertThat(running.status()).isEqualTo(SessionStatus.RUNNING);
        assertThat(running.startedAt()).contains(NOW);
    }

    @Test
    void reportsUnknownSession() {
        UUID missingId = UUID.randomUUID();
        assertThatThrownBy(() -> service.getSession(missingId))
            .isInstanceOf(SessionNotFoundException.class)
            .hasMessageContaining(missingId.toString());
    }

    private GameSession createSession() {
        return service.createSession(new CreateSessionCommand("Alpha Shift", "BTS Horizon", "Robert"));
    }

    private static final class InMemoryGameSessionRepository implements GameSessionRepository {
        private final Map<UUID, GameSession> sessions = new LinkedHashMap<>();
        private int saveCount;
        public GameSession save(GameSession session) { sessions.put(session.id(), session); saveCount++; return session; }
        public Optional<GameSession> findById(UUID sessionId) { return Optional.ofNullable(sessions.get(sessionId)); }
        public List<GameSession> findAll() { return List.copyOf(sessions.values()); }
        public boolean existsById(UUID sessionId) { return sessions.containsKey(sessionId); }
        public void deleteById(UUID sessionId) { sessions.remove(sessionId); }
    }
}
