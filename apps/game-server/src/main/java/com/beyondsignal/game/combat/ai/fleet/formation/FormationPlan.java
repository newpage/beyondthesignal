package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record FormationPlan(
    UUID fleetId,
    FormationTemplate template,
    FormationAnchor anchor,
    List<FormationAssignment> assignments
) {
    public FormationPlan {
        fleetId = Objects.requireNonNull(fleetId, "fleetId");
        template = Objects.requireNonNull(template, "template");
        anchor = Objects.requireNonNull(anchor, "anchor");
        assignments = List.copyOf(Objects.requireNonNull(assignments, "assignments"));
    }
}
