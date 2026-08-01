package com.beyondsignal.game.combat.ai.fleet.formation;

import com.beyondsignal.game.combat.ai.fleet.FleetDefinition;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class FleetFormationCoordinator {
    private final FormationCoordinator formationCoordinator;
    private final FormationUpdateProcessor updateProcessor;

    public FleetFormationCoordinator() {
        this(
            new FormationCoordinator(),
            new FormationUpdateProcessor()
        );
    }

    public FleetFormationCoordinator(
        FormationCoordinator formationCoordinator,
        FormationUpdateProcessor updateProcessor
    ) {
        this.formationCoordinator = Objects.requireNonNull(
            formationCoordinator,
            "formationCoordinator"
        );
        this.updateProcessor = Objects.requireNonNull(
            updateProcessor,
            "updateProcessor"
        );
    }

    public FormationPlan establish(
        FleetDefinition fleet,
        FormationType type,
        double spacing,
        FormationAnchor anchor
    ) {
        return formationCoordinator.initialize(
            fleet,
            type,
            spacing,
            anchor
        );
    }

    public FormationUpdate update(
        UUID fleetId,
        FormationType type,
        List<FormationAssignment> assignments,
        List<FormationMemberState> members
    ) {
        return updateProcessor.process(
            fleetId,
            type,
            assignments,
            members
        );
    }
}
