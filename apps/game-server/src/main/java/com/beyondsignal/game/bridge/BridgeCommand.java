package com.beyondsignal.game.bridge;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record BridgeCommand(
    UUID commandId,
    UUID playerId,
    BridgeStation station,
    BridgeCommandType type,
    Map<String, String> arguments
) {
    public BridgeCommand {
        commandId = Objects.requireNonNull(commandId, "commandId");
        playerId = Objects.requireNonNull(playerId, "playerId");
        station = Objects.requireNonNull(station, "station");
        type = Objects.requireNonNull(type, "type");
        arguments = Map.copyOf(Objects.requireNonNull(arguments, "arguments"));
    }

    public String required(String name) {
        String value = arguments.get(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing command argument: " + name);
        }
        return value;
    }
}
