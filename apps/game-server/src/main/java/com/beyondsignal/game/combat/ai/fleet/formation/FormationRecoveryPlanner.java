package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class FormationRecoveryPlanner {
    private final FormationIntegrityCalculator integrityCalculator;

    public FormationRecoveryPlanner() {
        this(new FormationIntegrityCalculator());
    }

    public FormationRecoveryPlanner(
        FormationIntegrityCalculator integrityCalculator
    ) {
        this.integrityCalculator = Objects.requireNonNull(
            integrityCalculator,
            "integrityCalculator"
        );
    }

    public FormationRecoveryPlan plan(List<FormationMemberState> members) {
        Objects.requireNonNull(members, "members");

        FormationMetrics metrics = integrityCalculator.calculate(members);
        FormationStatus status = integrityCalculator.status(metrics);

        List<FormationRecoveryAction> actions = members.stream()
            .filter(member -> !member.inPosition())
            .map(member -> new FormationRecoveryAction(
                member.participantId(),
                member.desiredPosition(),
                member.driftDistance(),
                urgency(member)
            ))
            .sorted(Comparator
                .comparingInt(FormationRecoveryAction::urgency)
                .reversed()
                .thenComparing(FormationRecoveryAction::participantId))
            .toList();

        return new FormationRecoveryPlan(status, actions);
    }

    private static int urgency(FormationMemberState member) {
        double tolerance = Math.max(member.tolerance(), 1.0);
        double ratio = member.driftDistance() / tolerance;
        return (int) Math.min(100, Math.round(ratio * 25.0));
    }
}
