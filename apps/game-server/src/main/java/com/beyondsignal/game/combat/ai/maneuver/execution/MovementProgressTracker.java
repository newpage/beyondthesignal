package com.beyondsignal.game.combat.ai.maneuver.execution;

public final class MovementProgressTracker {
    public double progress(double initialError, double currentError) {
        if (!Double.isFinite(initialError) || initialError < 0.0) {
            throw new IllegalArgumentException("initialError must be non-negative");
        }
        if (!Double.isFinite(currentError) || currentError < 0.0) {
            throw new IllegalArgumentException("currentError must be non-negative");
        }
        if (initialError == 0.0) {
            return 1.0;
        }
        return Math.max(0.0, Math.min(1.0, 1.0 - currentError / initialError));
    }
}
