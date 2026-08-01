package com.beyondsignal.game.combat.ai.fleet.formation;

import com.beyondsignal.game.combat.ai.fleet.FleetDefinition;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class FormationCoordinator {
    private final FormationPlanner planner;
    private final FormationIntegrityCalculator integrityCalculator;
    private final FormationRecoveryPlanner recoveryPlanner;

    public FormationCoordinator() {
        this(
            new FormationPlanner(),
            new FormationIntegrityCalculator(),
            new FormationRecoveryPlanner()
        );
    }

    public FormationCoordinator(
        FormationPlanner planner,
        FormationIntegrityCalculator integrityCalculator,
        FormationRecoveryPlanner recoveryPlanner
    ) {
        this.planner = Objects.requireNonNull(planner, "planner");
        this.integrityCalculator = Objects.requireNonNull(
            integrityCalculator,
            "integrityCalculator"
        );
        this.recoveryPlanner = Objects.requireNonNull(
            recoveryPlanner,
            "recoveryPlanner"
        );
    }

    public FormationPlan initialize(
        FleetDefinition fleet,
        FormationType type,
        double spacing,
        FormationAnchor anchor
    ) {
        FormationTemplate template = FormationTemplates.create(type, spacing);
        return planner.plan(fleet, template, anchor);
    }

    public FormationState evaluate(
        UUID fleetId,
        FormationType type,
        List<FormationAssignment> assignments,
        List<FormationMemberState> members
    ) {
        FormationMetrics metrics = integrityCalculator.calculate(members);
        return new FormationState(
            fleetId,
            type,
            integrityCalculator.status(metrics),
            assignments,
            members,
            metrics.integrity()
        );
    }

    public FormationRecoveryPlan recover(List<FormationMemberState> members) {
        return recoveryPlanner.plan(members);
    }
}
