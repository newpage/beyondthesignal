package com.beyondsignal.game.bridge;

import java.util.Objects;
import java.util.UUID;

public record BridgeCommandResult(
    UUID commandId,
    boolean accepted,
    String message,
    long stateVersion
) {
    public BridgeCommandResult {
        commandId = Objects.requireNonNull(commandId, "commandId");
        message = Objects.requireNonNull(message, "message");
        if (stateVersion < 0) {
            throw new IllegalArgumentException("stateVersion cannot be negative");
        }
    }

    public static BridgeCommandResult accepted(UUID id, String message, long version) {
        return new BridgeCommandResult(id, true, message, version);
    }

    public static BridgeCommandResult rejected(UUID id, String message, long version) {
        return new BridgeCommandResult(id, false, message, version);
    }
}
