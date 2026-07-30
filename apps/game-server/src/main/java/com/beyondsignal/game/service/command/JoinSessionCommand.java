package com.beyondsignal.game.service.command;
import java.util.Objects;
import java.util.UUID;
public record JoinSessionCommand(UUID sessionId, String displayName) {
    public JoinSessionCommand {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        if (displayName == null || displayName.isBlank()) throw new IllegalArgumentException("displayName must not be blank");
        displayName = displayName.trim();
    }
}
