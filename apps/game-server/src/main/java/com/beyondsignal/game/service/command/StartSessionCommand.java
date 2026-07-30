package com.beyondsignal.game.service.command;
import java.util.Objects;
import java.util.UUID;
public record StartSessionCommand(UUID sessionId, UUID requestingPlayerId) {
    public StartSessionCommand {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        Objects.requireNonNull(requestingPlayerId, "requestingPlayerId must not be null");
    }
}
