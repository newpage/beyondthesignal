package com.beyondsignal.game.combat.ai.maneuver.execution;

import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.Objects;
import java.util.UUID;

public record PursuitContext(
    UUID pursuerId,
    UUID targetId,
    CombatVector pursuerPosition,
    CombatVector pursuerVelocity,
    double maximumSpeed,
    CombatVector targetPosition,
    CombatVector targetVelocity,
    double desiredRange
) {
    public PursuitContext {
        pursuerId = Objects.requireNonNull(pursuerId, "pursuerId");
        targetId = Objects.requireNonNull(targetId, "targetId");
        pursuerPosition = Objects.requireNonNull(
            pursuerPosition,
            "pursuerPosition"
        );
        pursuerVelocity = Objects.requireNonNull(
            pursuerVelocity,
            "pursuerVelocity"
        );
        targetPosition = Objects.requireNonNull(
            targetPosition,
            "targetPosition"
        );
        targetVelocity = Objects.requireNonNull(
            targetVelocity,
            "targetVelocity"
        );
        if (!Double.isFinite(maximumSpeed) || maximumSpeed <= 0.0) {
            throw new IllegalArgumentException("maximumSpeed must be positive");
        }
        if (!Double.isFinite(desiredRange) || desiredRange < 0.0) {
            throw new IllegalArgumentException(
                "desiredRange must be non-negative"
            );
        }
    }

    public double currentDistance() {
        return pursuerPosition.distanceTo(targetPosition);
    }
}
