package com.beyondsignal.game.persistence;

import com.beyondsignal.game.domain.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository {
    Player save(UUID sessionId, Player player);

    Optional<Player> findById(UUID sessionId, UUID playerId);

    List<Player> findBySessionId(UUID sessionId);

    void delete(UUID sessionId, UUID playerId);
}
