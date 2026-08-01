package com.beyondsignal.game.combat.ai.maneuver.execution;

import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.Objects;
import java.util.UUID;

public record OrbitContext(
    UUID participantId,
    UUID targetId,
    CombatVector participantPosition,
    CombatVector participantVelocity,
    CombatVector targetPosition,
    CombatVector orbitNormal,
    double desiredRadius,
    double radialTolerance,
    double desiredTangentialSpeed
) {
    public OrbitContext {
        participantId = Objects.requireNonNull(participantId, "participantId");
        targetId = Objects.requireNonNull(targetId, "targetId");
        participantPosition = Objects.requireNonNull(
            participantPosition,
            "participantPosition"
        );
        participantVelocity = Objects.requireNonNull(
            participantVelocity,
            "participantVelocity"
        );
        targetPosition = Objects.requireNonNull(targetPosition, "targetPosition");
        orbitNormal = Objects.requireNonNull(orbitNormal, "orbitNormal");

        if (orbitNormal.magnitude() == 0.0) {
            throw new IllegalArgumentException("orbitNormal cannot be zero");
        }
        if (!Double.isFinite(desiredRadius) || desiredRadius <= 0.0) {
            throw new IllegalArgumentException("desiredRadius must be positive");
        }
        if (!Double.isFinite(radialTolerance) || radialTolerance < 0.0) {
            throw new IllegalArgumentException(
                "radialTolerance must be non-negative"
            );
        }
        if (!Double.isFinite(desiredTangentialSpeed)
            || desiredTangentialSpeed <= 0.0) {
            throw new IllegalArgumentException(
                "desiredTangentialSpeed must be positive"
            );
        }
    }

    public double currentRadius() {
        return participantPosition.distanceTo(targetPosition);
    }
}
