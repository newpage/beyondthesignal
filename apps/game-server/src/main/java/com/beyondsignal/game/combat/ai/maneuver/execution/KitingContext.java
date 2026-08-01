package com.beyondsignal.game.combat.ai.maneuver.execution;

import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.Objects;
import java.util.UUID;

public record KitingContext(
    UUID participantId,
    UUID targetId,
    CombatVector participantPosition,
    CombatVector targetPosition,
    CombatVector targetVelocity,
    double desiredRange,
    double rangeTolerance,
    double maximumSpeed
) {
    public KitingContext {
        participantId = Objects.requireNonNull(participantId, "participantId");
        targetId = Objects.requireNonNull(targetId, "targetId");
        participantPosition = Objects.requireNonNull(
            participantPosition,
            "participantPosition"
        );
        targetPosition = Objects.requireNonNull(targetPosition, "targetPosition");
        targetVelocity = Objects.requireNonNull(targetVelocity, "targetVelocity");

        if (!Double.isFinite(desiredRange) || desiredRange <= 0.0) {
            throw new IllegalArgumentException("desiredRange must be positive");
        }
        if (!Double.isFinite(rangeTolerance) || rangeTolerance < 0.0) {
            throw new IllegalArgumentException(
                "rangeTolerance must be non-negative"
            );
        }
        if (!Double.isFinite(maximumSpeed) || maximumSpeed <= 0.0) {
            throw new IllegalArgumentException("maximumSpeed must be positive");
        }
    }

    public double currentRange() {
        return participantPosition.distanceTo(targetPosition);
    }
}
