package com.beyondsignal.game.combat.ai.fleet;

import java.util.Objects;
import java.util.UUID;

public record FleetMember(
    UUID participantId,
    FleetRole role,
    int commandPriority
) {
    public FleetMember {
        participantId = Objects.requireNonNull(participantId, "participantId");
        role = Objects.requireNonNull(role, "role");
        if (commandPriority < 0) {
            throw new IllegalArgumentException("commandPriority cannot be negative");
        }
    }
}
