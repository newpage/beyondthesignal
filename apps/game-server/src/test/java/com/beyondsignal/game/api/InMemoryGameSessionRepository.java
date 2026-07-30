package com.beyondsignal.game.api;

import com.beyondsignal.game.domain.GameSession;
import com.beyondsignal.game.persistence.GameSessionRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

final class InMemoryGameSessionRepository implements GameSessionRepository {
    private final Map<UUID, GameSession> sessions = new LinkedHashMap<>();

    @Override
    public GameSession save(GameSession session) {
        sessions.put(session.id(), session);
        return session;
    }

    @Override
    public Optional<GameSession> findById(UUID id) {
        return Optional.ofNullable(sessions.get(id));
    }

    @Override
    public List<GameSession> findAll() {
        return new ArrayList<>(sessions.values());
    }

    @Override
    public boolean existsById(UUID id) {
        return sessions.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        sessions.remove(id);
    }
}
