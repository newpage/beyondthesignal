package com.beyondsignal.game.combat.ai.fleet.formation;

import com.beyondsignal.game.combat.ai.fleet.FleetDefinition;
import com.beyondsignal.game.combat.ai.fleet.FleetRole;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class CommanderProtectionPlanner {
    public List<FormationProtectionOrder> plan(
        FleetDefinition fleet,
        FormationPlan formationPlan
    ) {
        Objects.requireNonNull(fleet, "fleet");
        Objects.requireNonNull(formationPlan, "formationPlan");

        UUID commanderId = fleet.commander()
            .map(member -> member.participantId())
            .orElseThrow(() -> new IllegalStateException("Fleet has no commander"));

        return fleet.members().stream()
            .filter(member -> member.role() == FleetRole.ESCORT)
            .map(member -> {
                FormationAssignment assignment = formationPlan.assignments().stream()
                    .filter(value -> value.participantId().equals(member.participantId()))
                    .findFirst()
                    .orElseThrow();

                return new FormationProtectionOrder(
                    member.participantId(),
                    commanderId,
                    assignment.desiredPosition(),
                    90
                );
            })
            .sorted(Comparator.comparing(FormationProtectionOrder::protectorId))
            .toList();
    }
}
