package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.Objects;
import java.util.UUID;

public record FormationMemberCombatState(
    UUID participantId,
    double hullPercentage,
    double shieldPercentage,
    boolean underDirectThreat,
    boolean commander,
    FormationCombatState state
) {
    public FormationMemberCombatState {
        participantId = Objects.requireNonNull(participantId, "participantId");
        validateUnit(hullPercentage, "hullPercentage");
        validateUnit(shieldPercentage, "shieldPercentage");
        state = Objects.requireNonNull(state, "state");
    }

    private static void validateUnit(double value, String name) {
        if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
    }
}
