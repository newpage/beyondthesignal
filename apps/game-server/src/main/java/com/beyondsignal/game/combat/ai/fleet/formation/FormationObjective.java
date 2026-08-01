package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record FormationObjective(
    UUID fleetId,
    FormationObjectiveType type,
    FormationType formationType,
    int priority,
    Map<String, Integer> reasons
) {
    public FormationObjective {
        fleetId = Objects.requireNonNull(fleetId, "fleetId");
        type = Objects.requireNonNull(type, "type");
        formationType = Objects.requireNonNull(formationType, "formationType");
        if (priority < 0 || priority > 100) {
            throw new IllegalArgumentException("priority must be between 0 and 100");
        }
        reasons = Map.copyOf(Objects.requireNonNull(reasons, "reasons"));
    }
}
