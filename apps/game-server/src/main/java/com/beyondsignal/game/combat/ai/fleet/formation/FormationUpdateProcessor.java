package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class FormationUpdateProcessor {
    private final FormationCoordinator coordinator;
    private final FormationObjectiveSelector objectiveSelector;
    private final FormationOrderGenerator orderGenerator;

    public FormationUpdateProcessor() {
        this(
            new FormationCoordinator(),
            new FormationObjectiveSelector(),
            new FormationOrderGenerator()
        );
    }

    public FormationUpdateProcessor(
        FormationCoordinator coordinator,
        FormationObjectiveSelector objectiveSelector,
        FormationOrderGenerator orderGenerator
    ) {
        this.coordinator = Objects.requireNonNull(coordinator, "coordinator");
        this.objectiveSelector = Objects.requireNonNull(
            objectiveSelector,
            "objectiveSelector"
        );
        this.orderGenerator = Objects.requireNonNull(
            orderGenerator,
            "orderGenerator"
        );
    }

    public FormationUpdate process(
        UUID fleetId,
        FormationType type,
        List<FormationAssignment> assignments,
        List<FormationMemberState> members
    ) {
        FormationState state = coordinator.evaluate(
            fleetId,
            type,
            assignments,
            members
        );
        FormationObjective objective = objectiveSelector.select(
            fleetId,
            state
        );
        FormationOrderSet orders = orderGenerator.generate(
            state,
            objective
        );
        FormationRecoveryPlan recovery = coordinator.recover(members);

        return new FormationUpdate(
            state,
            objective,
            orders,
            recovery
        );
    }
}
