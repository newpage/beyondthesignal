package com.beyondsignal.game.combat.ai.tactical;

public record TargetSelectionPolicy(
    boolean avoidOverkill,
    boolean prioritizeCommanders,
    boolean prioritizeSupport,
    int overkillTolerance
) {
    public TargetSelectionPolicy {
        if (overkillTolerance < 0) {
            throw new IllegalArgumentException("overkillTolerance cannot be negative");
        }
    }

    public static TargetSelectionPolicy standard() {
        return new TargetSelectionPolicy(true, true, true, 10);
    }
}
