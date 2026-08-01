package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class FormationValidator {
    public void validateAssignments(
        FormationTemplate template,
        List<FormationAssignment> assignments
    ) {
        Objects.requireNonNull(template, "template");
        Objects.requireNonNull(assignments, "assignments");

        Set<UUID> participants = new HashSet<>();
        Set<Integer> slots = new HashSet<>();

        for (FormationAssignment assignment : assignments) {
            if (!participants.add(assignment.participantId())) {
                throw new IllegalArgumentException(
                    "Participant has multiple formation assignments: "
                        + assignment.participantId()
                );
            }
            if (!slots.add(assignment.slot().index())) {
                throw new IllegalArgumentException(
                    "Formation slot assigned more than once: "
                        + assignment.slot().index()
                );
            }
            template.slot(assignment.slot().index());
        }
    }
}
