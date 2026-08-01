package com.beyondsignal.game.combat.ai.maneuver.execution;

import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record KitingPlan(
    UUID participantId,
    UUID targetId,
    RangeControlState state,
    CombatVector desiredVelocity,
    double currentRange,
    double desiredRange,
    int confidence,
    Map<String, Integer> reasons
) {
    public KitingPlan {
        participantId = Objects.requireNonNull(participantId, "participantId");
        targetId = Objects.requireNonNull(targetId, "targetId");
        state = Objects.requireNonNull(state, "state");
        desiredVelocity = Objects.requireNonNull(
            desiredVelocity,
            "desiredVelocity"
        );
        if (!Double.isFinite(currentRange) || currentRange < 0.0) {
            throw new IllegalArgumentException(
                "currentRange must be non-negative"
            );
        }
        if (!Double.isFinite(desiredRange) || desiredRange <= 0.0) {
            throw new IllegalArgumentException("desiredRange must be positive");
        }
        if (confidence < 0 || confidence > 100) {
            throw new IllegalArgumentException(
                "confidence must be between 0 and 100"
            );
        }
        reasons = Map.copyOf(Objects.requireNonNull(reasons, "reasons"));
    }
}
