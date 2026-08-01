package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class FormationObjectiveSelector {
    public FormationObjective select(UUID fleetId, FormationState state) {
        Objects.requireNonNull(fleetId, "fleetId");
        Objects.requireNonNull(state, "state");

        FormationObjectiveType type;
        int priority;
        Map<String, Integer> reasons = new LinkedHashMap<>();

        switch (state.status()) {
            case STABLE -> {
                type = FormationObjectiveType.MAINTAIN_FORMATION;
                priority = 35;
                reasons.put("formationStable", 35);
            }
            case FORMING -> {
                type = FormationObjectiveType.FORM_UP;
                priority = 60;
                reasons.put("formationIncomplete", 60);
            }
            case DEGRADED -> {
                type = FormationObjectiveType.RECOVER_FORMATION;
                priority = 80;
                reasons.put("formationDegraded", 80);
            }
            case BROKEN -> {
                type = FormationObjectiveType.RECOVER_FORMATION;
                priority = 100;
                reasons.put("formationBroken", 100);
            }
            default -> throw new IllegalStateException(
                "Unhandled formation status: " + state.status()
            );
        }

        return new FormationObjective(
            fleetId,
            type,
            state.type(),
            priority,
            reasons
        );
    }
}
