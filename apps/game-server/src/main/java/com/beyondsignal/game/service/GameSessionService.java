package com.beyondsignal.game.service;

import com.beyondsignal.game.domain.GameSession;
import com.beyondsignal.game.domain.Player;
import com.beyondsignal.game.domain.SessionStatus;
import com.beyondsignal.game.persistence.GameSessionRepository;
import com.beyondsignal.game.service.command.AssignStationCommand;
import com.beyondsignal.game.service.command.CreateSessionCommand;
import com.beyondsignal.game.service.command.JoinSessionCommand;
import com.beyondsignal.game.service.command.LeaveSessionCommand;
import com.beyondsignal.game.service.command.StartSessionCommand;
import com.beyondsignal.game.service.command.UnassignStationCommand;
import com.beyondsignal.game.service.event.NoOpSessionEventPublisher;
import com.beyondsignal.game.service.event.SessionEvent;
import com.beyondsignal.game.service.event.SessionEventPublisher;
import com.beyondsignal.game.service.event.SessionEventType;
import com.beyondsignal.game.simulation.runtime.NoOpSimulationSessionLifecycle;
import com.beyondsignal.game.simulation.runtime.SimulationSessionLifecycle;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class GameSessionService {
    private final GameSessionRepository repository;
    private final Clock clock;
    private final SessionEventPublisher eventPublisher;
    private final SimulationSessionLifecycle simulationLifecycle;

    public GameSessionService(GameSessionRepository repository, Clock clock) {
        this(repository, clock, NoOpSessionEventPublisher.INSTANCE, NoOpSimulationSessionLifecycle.INSTANCE);
    }

    public GameSessionService(
        GameSessionRepository repository,
        Clock clock,
        SessionEventPublisher eventPublisher
    ) {
        this(repository, clock, eventPublisher, NoOpSimulationSessionLifecycle.INSTANCE);
    }

    public GameSessionService(
        GameSessionRepository repository,
        Clock clock,
        SessionEventPublisher eventPublisher,
        SimulationSessionLifecycle simulationLifecycle
    ) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher must not be null");
        this.simulationLifecycle = Objects.requireNonNull(simulationLifecycle, "simulationLifecycle must not be null");
    }

    public GameSession createSession(CreateSessionCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        Instant now = clock.instant();
        GameSession session = GameSession.create(command.sessionName(), command.shipName(), command.hostDisplayName(), now);
        session.transitionTo(SessionStatus.WAITING_FOR_PLAYERS, now);
        GameSession saved = repository.save(session);
        publish(saved.id(), SessionEventType.SESSION_CREATED, now, Map.of(
            "hostPlayerId", saved.hostPlayerId().toString(),
            "status", saved.status().name()
        ));
        return saved;
    }

    public Player joinSession(JoinSessionCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        GameSession session = requireSession(command.sessionId());
        Instant now = clock.instant();
        Player player = Player.crewMember(command.displayName(), now);
        session.addPlayer(player);
        repository.save(session);
        publish(session.id(), SessionEventType.PLAYER_JOINED, now, Map.of(
            "playerId", player.id().toString(),
            "displayName", player.displayName()
        ));
        return player;
    }

    public GameSession leaveSession(LeaveSessionCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        GameSession session = requireSession(command.sessionId());
        session.removePlayer(command.playerId());
        GameSession saved = repository.save(session);
        publish(saved.id(), SessionEventType.PLAYER_LEFT, clock.instant(), Map.of(
            "playerId", command.playerId().toString()
        ));
        return saved;
    }

    public GameSession assignStation(AssignStationCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        GameSession session = requireSession(command.sessionId());
        requireHost(session, command.requestingPlayerId());
        Instant now = clock.instant();
        session.assignStation(command.station(), command.playerId(), now);
        GameSession saved = repository.save(session);
        publish(saved.id(), SessionEventType.STATION_ASSIGNED, now, Map.of(
            "station", command.station().name(),
            "playerId", command.playerId().toString()
        ));
        return saved;
    }

    public GameSession unassignStation(UnassignStationCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        GameSession session = requireSession(command.sessionId());
        requireHost(session, command.requestingPlayerId());
        session.unassignStation(command.station());
        GameSession saved = repository.save(session);
        publish(saved.id(), SessionEventType.STATION_UNASSIGNED, clock.instant(), Map.of(
            "station", command.station().name()
        ));
        return saved;
    }

    public GameSession markReady(UUID sessionId, UUID requestingPlayerId) {
        GameSession session = requireSession(sessionId);
        requireHost(session, requestingPlayerId);
        Instant now = clock.instant();
        session.transitionTo(SessionStatus.READY, now);
        GameSession saved = repository.save(session);
        publish(saved.id(), SessionEventType.SESSION_READY, now, Map.of(
            "status", saved.status().name()
        ));
        return saved;
    }

    public GameSession startSession(StartSessionCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        GameSession session = requireSession(command.sessionId());
        requireHost(session, command.requestingPlayerId());
        Instant now = clock.instant();
        session.transitionTo(SessionStatus.STARTING, now);
        session.transitionTo(SessionStatus.RUNNING, now);
        GameSession saved = repository.save(session);
        simulationLifecycle.start(saved.id());
        publish(saved.id(), SessionEventType.SESSION_STARTED, now, Map.of(
            "status", saved.status().name(),
            "startedAt", saved.startedAt().orElseThrow().toString()
        ));
        return saved;
    }

    public GameSession getSession(UUID sessionId) { return requireSession(sessionId); }
    public List<GameSession> listSessions() { return repository.findAll(); }

    private void publish(UUID sessionId, SessionEventType type, Instant occurredAt, Map<String, Object> payload) {
        eventPublisher.publish(new SessionEvent(sessionId, type, occurredAt, payload));
    }

    private GameSession requireSession(UUID sessionId) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        return repository.findById(sessionId).orElseThrow(() -> new SessionNotFoundException(sessionId));
    }

    private static void requireHost(GameSession session, UUID requestingPlayerId) {
        Objects.requireNonNull(requestingPlayerId, "requestingPlayerId must not be null");
        if (!session.hostPlayerId().equals(requestingPlayerId)) {
            throw new SessionAuthorizationException("Only the session host may perform this operation");
        }
    }
}
