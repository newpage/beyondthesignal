package com.beyondsignal.game.persistence;

import com.beyondsignal.game.domain.GameSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameSessionRepository {
    GameSession save(GameSession session);

    Optional<GameSession> findById(UUID sessionId);

    List<GameSession> findAll();

    boolean existsById(UUID sessionId);

    void deleteById(UUID sessionId);
}
