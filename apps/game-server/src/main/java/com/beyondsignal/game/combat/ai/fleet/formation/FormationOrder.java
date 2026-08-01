package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.Objects;
import java.util.UUID;

public record FormationOrder(
    UUID participantId,
    FormationObjective objective,
    FormationAssignment assignment,
    FormationMovementIntent movementIntent
) {
    public FormationOrder {
        participantId = Objects.requireNonNull(participantId, "participantId");
        objective = Objects.requireNonNull(objective, "objective");
        assignment = Objects.requireNonNull(assignment, "assignment");
        movementIntent = Objects.requireNonNull(movementIntent, "movementIntent");

        if (!participantId.equals(assignment.participantId())
            || !participantId.equals(movementIntent.participantId())) {
            throw new IllegalArgumentException(
                "Formation order components must belong to the same participant"
            );
        }
    }
}
