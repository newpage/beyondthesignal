package com.beyondsignal.game.combat.ai.goal;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record CombatAiGoal(
    CombatAiGoalType type,
    UUID targetId,
    int priority,
    Map<String, Integer> reasons
) {
    public CombatAiGoal {
        type = Objects.requireNonNull(type, "type");
        if (priority < 0) {
            throw new IllegalArgumentException("priority cannot be negative");
        }
        reasons = Map.copyOf(Objects.requireNonNull(reasons, "reasons"));
    }
}
