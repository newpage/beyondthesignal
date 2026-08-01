package com.beyondsignal.game.combat.ai.maneuver.execution;

import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.Objects;

public record MovementPrediction(
    CombatVector currentPosition,
    CombatVector predictedPosition,
    CombatVector velocity,
    double horizonSeconds
) {
    public MovementPrediction {
        currentPosition = Objects.requireNonNull(currentPosition, "currentPosition");
        predictedPosition = Objects.requireNonNull(
            predictedPosition,
            "predictedPosition"
        );
        velocity = Objects.requireNonNull(velocity, "velocity");
        if (!Double.isFinite(horizonSeconds) || horizonSeconds < 0.0) {
            throw new IllegalArgumentException(
                "horizonSeconds must be non-negative"
            );
        }
    }

    public double displacement() {
        return currentPosition.distanceTo(predictedPosition);
    }
}
