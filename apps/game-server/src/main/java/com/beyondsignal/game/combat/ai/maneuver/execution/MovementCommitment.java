package com.beyondsignal.game.combat.ai.maneuver.execution;

public record MovementCommitment(
    long startTick,
    long minimumCommitmentTicks,
    long maximumCommitmentTicks
) {
    public MovementCommitment {
        if (startTick < 0) {
            throw new IllegalArgumentException("startTick cannot be negative");
        }
        if (minimumCommitmentTicks < 0) {
            throw new IllegalArgumentException(
                "minimumCommitmentTicks cannot be negative"
            );
        }
        if (maximumCommitmentTicks < minimumCommitmentTicks) {
            throw new IllegalArgumentException(
                "maximumCommitmentTicks cannot be below minimum"
            );
        }
    }

    public boolean mayReplan(long tick) {
        return tick - startTick >= minimumCommitmentTicks;
    }

    public boolean mustReplan(long tick) {
        return tick - startTick >= maximumCommitmentTicks;
    }
}
