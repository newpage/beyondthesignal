package com.beyondsignal.game.combat.ai.fleet.formation;

import com.beyondsignal.game.combat.ai.fleet.FleetDefinition;
import com.beyondsignal.game.combat.ai.fleet.FleetMember;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class FormationReassignmentPlanner {
    private final FormationPlanner planner;

    public FormationReassignmentPlanner() {
        this(new FormationPlanner());
    }

    public FormationReassignmentPlanner(FormationPlanner planner) {
        this.planner = Objects.requireNonNull(planner, "planner");
    }

    public FormationPlan reassignAfterLoss(
        FleetDefinition fleet,
        Set<UUID> unavailableParticipantIds,
        FormationTemplate template,
        FormationAnchor anchor
    ) {
        Objects.requireNonNull(fleet, "fleet");
        Objects.requireNonNull(unavailableParticipantIds, "unavailableParticipantIds");

        List<FleetMember> survivors = fleet.members().stream()
            .filter(member -> !unavailableParticipantIds.contains(member.participantId()))
            .toList();

        if (survivors.isEmpty()) {
            throw new IllegalStateException("Cannot form a formation without survivors");
        }

        FleetDefinition survivingFleet = new FleetDefinition(
            fleet.fleetId(),
            fleet.doctrine(),
            survivors
        );

        return planner.plan(survivingFleet, template, anchor);
    }
}
