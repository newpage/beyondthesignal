package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record FormationCombatDecision(
    UUID participantId,
    FormationCombatState state,
    int priority,
    Map<String, Integer> reasons
) {
    public FormationCombatDecision {
        participantId = Objects.requireNonNull(participantId, "participantId");
        state = Objects.requireNonNull(state, "state");
        if (priority < 0 || priority > 100) {
            throw new IllegalArgumentException("priority must be between 0 and 100");
        }
        reasons = Map.copyOf(Objects.requireNonNull(reasons, "reasons"));
    }
}
