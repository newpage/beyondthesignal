package com.beyondsignal.game.combat.ai.fleet;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record FleetObjective(
    FleetObjectiveType type,
    UUID targetId,
    int priority,
    Map<String, Integer> reasons
) {
    public FleetObjective {
        type = Objects.requireNonNull(type, "type");
        if (priority < 0) {
            throw new IllegalArgumentException("priority cannot be negative");
        }
        reasons = Map.copyOf(Objects.requireNonNull(reasons, "reasons"));
    }
}
