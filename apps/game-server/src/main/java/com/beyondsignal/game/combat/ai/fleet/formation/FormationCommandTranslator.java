package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.Objects;

public final class FormationCommandTranslator {
    public FormationMovementIntent translate(
        FormationAssignment assignment,
        FormationMemberState memberState,
        FormationObjective objective
    ) {
        Objects.requireNonNull(assignment, "assignment");
        Objects.requireNonNull(memberState, "memberState");
        Objects.requireNonNull(objective, "objective");

        if (!assignment.participantId().equals(memberState.participantId())) {
            throw new IllegalArgumentException(
                "Assignment and member state belong to different participants"
            );
        }

        double drift = memberState.driftDistance();
        boolean hold = memberState.inPosition()
            && objective.type() == FormationObjectiveType.MAINTAIN_FORMATION;

        int urgency = hold
            ? 0
            : Math.min(
                100,
                Math.max(
                    objective.priority(),
                    (int) Math.round(
                        drift / Math.max(memberState.tolerance(), 1.0) * 20.0
                    )
                )
            );

        return new FormationMovementIntent(
            assignment.participantId(),
            assignment.desiredPosition(),
            drift,
            urgency,
            hold
        );
    }
}
