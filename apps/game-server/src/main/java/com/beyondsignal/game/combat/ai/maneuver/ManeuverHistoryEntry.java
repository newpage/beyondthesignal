package com.beyondsignal.game.combat.ai.maneuver;

import java.util.Objects;

public record ManeuverHistoryEntry(
    long tick,
    ManeuverDecision decision,
    ManeuverState state
) {
    public ManeuverHistoryEntry {
        if (tick < 0) {
            throw new IllegalArgumentException("tick cannot be negative");
        }
        decision = Objects.requireNonNull(decision, "decision");
        state = Objects.requireNonNull(state, "state");
    }
}
