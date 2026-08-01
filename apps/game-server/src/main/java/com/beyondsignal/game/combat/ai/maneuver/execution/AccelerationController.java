package com.beyondsignal.game.combat.ai.maneuver.execution;

public final class AccelerationController {
    public double command(
        double currentSpeed,
        double desiredSpeed,
        double maximumAcceleration
    ) {
        if (!Double.isFinite(currentSpeed) || currentSpeed < 0.0) {
            throw new IllegalArgumentException("currentSpeed must be non-negative");
        }
        if (!Double.isFinite(desiredSpeed) || desiredSpeed < 0.0) {
            throw new IllegalArgumentException("desiredSpeed must be non-negative");
        }
        if (!Double.isFinite(maximumAcceleration)
            || maximumAcceleration <= 0.0) {
            throw new IllegalArgumentException(
                "maximumAcceleration must be positive"
            );
        }

        return Math.max(
            -maximumAcceleration,
            Math.min(maximumAcceleration, desiredSpeed - currentSpeed)
        );
    }
}
