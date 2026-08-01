package com.beyondsignal.game.combat.ai.maneuver.execution;

public record MovementLimits(
    double maximumSpeed,
    double maximumAcceleration,
    double maximumTurnRateDegrees,
    double positionTolerance,
    double velocityTolerance
) {
    public MovementLimits {
        requirePositive(maximumSpeed, "maximumSpeed");
        requirePositive(maximumAcceleration, "maximumAcceleration");
        requirePositive(maximumTurnRateDegrees, "maximumTurnRateDegrees");
        requireNonNegative(positionTolerance, "positionTolerance");
        requireNonNegative(velocityTolerance, "velocityTolerance");
    }

    private static void requirePositive(double value, String name) {
        if (!Double.isFinite(value) || value <= 0.0) {
            throw new IllegalArgumentException(name + " must be positive");
        }
    }

    private static void requireNonNegative(double value, String name) {
        if (!Double.isFinite(value) || value < 0.0) {
            throw new IllegalArgumentException(name + " must be non-negative");
        }
    }
}
