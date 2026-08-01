package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.List;
import java.util.Objects;

public final class FormationPositionCalculator {
    public List<FormationAssignment> recalculate(
        List<FormationAssignment> assignments,
        FormationAnchor anchor,
        double spacing
    ) {
        Objects.requireNonNull(assignments, "assignments");
        Objects.requireNonNull(anchor, "anchor");
        if (!Double.isFinite(spacing) || spacing <= 0.0) {
            throw new IllegalArgumentException("spacing must be positive");
        }

        return assignments.stream()
            .map(assignment -> new FormationAssignment(
                assignment.participantId(),
                assignment.slot(),
                anchor.worldPosition(assignment.slot(), spacing)
            ))
            .toList();
    }
}
