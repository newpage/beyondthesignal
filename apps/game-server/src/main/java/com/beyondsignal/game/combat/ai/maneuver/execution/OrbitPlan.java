package com.beyondsignal.game.combat.ai.maneuver.execution;

import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record OrbitPlan(
    UUID participantId,
    UUID targetId,
    OrbitState state,
    OrbitDirection direction,
    CombatVector desiredPosition,
    CombatVector desiredVelocity,
    double radiusError,
    int confidence,
    Map<String, Integer> reasons
) {
    public OrbitPlan {
        participantId = Objects.requireNonNull(participantId, "participantId");
        targetId = Objects.requireNonNull(targetId, "targetId");
        state = Objects.requireNonNull(state, "state");
        direction = Objects.requireNonNull(direction, "direction");
        desiredPosition = Objects.requireNonNull(
            desiredPosition,
            "desiredPosition"
        );
        desiredVelocity = Objects.requireNonNull(
            desiredVelocity,
            "desiredVelocity"
        );
        if (!Double.isFinite(radiusError)) {
            throw new IllegalArgumentException("radiusError must be finite");
        }
        if (confidence < 0 || confidence > 100) {
            throw new IllegalArgumentException(
                "confidence must be between 0 and 100"
            );
        }
        reasons = Map.copyOf(Objects.requireNonNull(reasons, "reasons"));
    }
}
