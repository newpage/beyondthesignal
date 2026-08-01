package com.beyondsignal.game.combat.ai.maneuver.execution;

public record RangeControlDecision(
    RangeControlState state,
    double rangeError,
    double speedCommand
) {
    public RangeControlDecision {
        if (!Double.isFinite(rangeError)) {
            throw new IllegalArgumentException("rangeError must be finite");
        }
        if (!Double.isFinite(speedCommand)) {
            throw new IllegalArgumentException("speedCommand must be finite");
        }
    }
}
