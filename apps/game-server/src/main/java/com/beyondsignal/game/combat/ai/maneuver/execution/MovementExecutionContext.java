package com.beyondsignal.game.combat.ai.maneuver.execution;

import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.Objects;
import java.util.UUID;

public record MovementExecutionContext(
    UUID participantId,
    long tick,
    CombatVector currentPosition,
    CombatVector currentVelocity,
    CombatVector currentHeading,
    CombatVector desiredPosition,
    CombatVector desiredVelocity,
    MovementLimits limits
) {
    public MovementExecutionContext {
        participantId = Objects.requireNonNull(participantId, "participantId");
        if (tick < 0) {
            throw new IllegalArgumentException("tick cannot be negative");
        }
        currentPosition = Objects.requireNonNull(currentPosition, "currentPosition");
        currentVelocity = Objects.requireNonNull(currentVelocity, "currentVelocity");
        currentHeading = Objects.requireNonNull(currentHeading, "currentHeading");
        desiredPosition = Objects.requireNonNull(desiredPosition, "desiredPosition");
        desiredVelocity = Objects.requireNonNull(desiredVelocity, "desiredVelocity");
        limits = Objects.requireNonNull(limits, "limits");
        if (currentHeading.magnitude() == 0.0) {
            throw new IllegalArgumentException("currentHeading cannot be zero");
        }
    }

    public double positionError() {
        return currentPosition.distanceTo(desiredPosition);
    }

    public double velocityError() {
        return currentVelocity.distanceTo(desiredVelocity);
    }
}
