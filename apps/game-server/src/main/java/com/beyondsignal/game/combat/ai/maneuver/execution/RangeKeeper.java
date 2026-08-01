package com.beyondsignal.game.combat.ai.maneuver.execution;

public final class RangeKeeper {
    public RangeControlDecision control(
        double currentRange,
        double desiredRange,
        double tolerance,
        double maximumSpeed
    ) {
        if (!Double.isFinite(currentRange) || currentRange < 0.0) {
            throw new IllegalArgumentException(
                "currentRange must be non-negative"
            );
        }
        if (!Double.isFinite(desiredRange) || desiredRange <= 0.0) {
            throw new IllegalArgumentException("desiredRange must be positive");
        }
        if (!Double.isFinite(tolerance) || tolerance < 0.0) {
            throw new IllegalArgumentException("tolerance must be non-negative");
        }
        if (!Double.isFinite(maximumSpeed) || maximumSpeed <= 0.0) {
            throw new IllegalArgumentException("maximumSpeed must be positive");
        }

        double error = currentRange - desiredRange;

        if (Math.abs(error) <= tolerance) {
            return new RangeControlDecision(
                RangeControlState.IN_BAND,
                error,
                0.0
            );
        }

        double proportionalSpeed = Math.min(
            maximumSpeed,
            Math.abs(error)
        );

        return error < 0.0
            ? new RangeControlDecision(
                RangeControlState.TOO_CLOSE,
                error,
                -proportionalSpeed
            )
            : new RangeControlDecision(
                RangeControlState.TOO_FAR,
                error,
                proportionalSpeed
            );
    }
}
