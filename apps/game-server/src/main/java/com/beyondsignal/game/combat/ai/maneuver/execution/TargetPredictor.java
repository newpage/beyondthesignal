package com.beyondsignal.game.combat.ai.maneuver.execution;

import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.Objects;

public final class TargetPredictor {
    public MovementPrediction predict(
        CombatVector currentPosition,
        CombatVector velocity,
        double horizonSeconds
    ) {
        Objects.requireNonNull(currentPosition, "currentPosition");
        Objects.requireNonNull(velocity, "velocity");
        if (!Double.isFinite(horizonSeconds) || horizonSeconds < 0.0) {
            throw new IllegalArgumentException(
                "horizonSeconds must be non-negative"
            );
        }

        CombatVector predicted = currentPosition.add(
            velocity.scale(horizonSeconds)
        );

        return new MovementPrediction(
            currentPosition,
            predicted,
            velocity,
            horizonSeconds
        );
    }
}
