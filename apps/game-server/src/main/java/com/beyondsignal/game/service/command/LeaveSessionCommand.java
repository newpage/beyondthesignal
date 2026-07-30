package com.beyondsignal.game.service.command;
import java.util.Objects;
import java.util.UUID;
public record LeaveSessionCommand(UUID sessionId, UUID playerId) {
    public LeaveSessionCommand {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        Objects.requireNonNull(playerId, "playerId must not be null");
    }
}
