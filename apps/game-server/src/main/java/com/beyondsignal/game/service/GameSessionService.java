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

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class GameSessionService {
    private final GameSessionRepository repository;
    private final Clock clock;

    public GameSessionService(GameSessionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public GameSession createSession(CreateSessionCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        Instant now = clock.instant();
        GameSession session = GameSession.create(command.sessionName(), command.shipName(), command.hostDisplayName(), now);
        session.transitionTo(SessionStatus.WAITING_FOR_PLAYERS, now);
        return repository.save(session);
    }

    public Player joinSession(JoinSessionCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        GameSession session = requireSession(command.sessionId());
        Player player = Player.crewMember(command.displayName(), clock.instant());
        session.addPlayer(player);
        repository.save(session);
        return player;
    }

    public GameSession leaveSession(LeaveSessionCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        GameSession session = requireSession(command.sessionId());
        session.removePlayer(command.playerId());
        return repository.save(session);
    }

    public GameSession assignStation(AssignStationCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        GameSession session = requireSession(command.sessionId());
        requireHost(session, command.requestingPlayerId());
        session.assignStation(command.station(), command.playerId(), clock.instant());
        return repository.save(session);
    }

    public GameSession unassignStation(UnassignStationCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        GameSession session = requireSession(command.sessionId());
        requireHost(session, command.requestingPlayerId());
        session.unassignStation(command.station());
        return repository.save(session);
    }

    public GameSession markReady(UUID sessionId, UUID requestingPlayerId) {
        GameSession session = requireSession(sessionId);
        requireHost(session, requestingPlayerId);
        session.transitionTo(SessionStatus.READY, clock.instant());
        return repository.save(session);
    }

    public GameSession startSession(StartSessionCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        GameSession session = requireSession(command.sessionId());
        requireHost(session, command.requestingPlayerId());
        Instant now = clock.instant();
        session.transitionTo(SessionStatus.STARTING, now);
        session.transitionTo(SessionStatus.RUNNING, now);
        return repository.save(session);
    }

    public GameSession getSession(UUID sessionId) { return requireSession(sessionId); }
    public List<GameSession> listSessions() { return repository.findAll(); }

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
