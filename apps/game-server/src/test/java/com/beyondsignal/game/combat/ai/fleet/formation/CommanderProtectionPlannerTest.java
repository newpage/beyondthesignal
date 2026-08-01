package com.beyondsignal.game.combat.ai.fleet.formation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.beyondsignal.game.combat.ai.fleet.FleetDefinition;
import com.beyondsignal.game.combat.ai.fleet.FleetDoctrine;
import com.beyondsignal.game.combat.ai.fleet.FleetMember;
import com.beyondsignal.game.combat.ai.fleet.FleetRole;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CommanderProtectionPlannerTest {
    @Test
    void createsProtectionOrdersForEscortsOnly() {
        UUID commander = UUID.randomUUID();
        UUID escort = UUID.randomUUID();
        UUID strike = UUID.randomUUID();

        FleetDefinition fleet = new FleetDefinition(
            UUID.randomUUID(),
            FleetDoctrine.DEFENSIVE_SCREEN,
            List.of(
                new FleetMember(commander, FleetRole.COMMANDER, 100),
                new FleetMember(escort, FleetRole.ESCORT, 80),
                new FleetMember(strike, FleetRole.STRIKE, 60)
            )
        );

        FormationTemplate template =
            FormationTemplates.create(FormationType.WEDGE, 100.0);
        FormationAnchor anchor = anchor(commander);
        FormationPlan plan = new FormationPlanner().plan(
            fleet,
            template,
            anchor
        );

        List<FormationProtectionOrder> orders =
            new CommanderProtectionPlanner().plan(fleet, plan);

        assertEquals(1, orders.size());
        assertEquals(escort, orders.getFirst().protectorId());
        assertEquals(commander, orders.getFirst().commanderId());
    }

    private static FormationAnchor anchor(UUID commander) {
        return new FormationAnchor(
            commander,
            FormationVector.zero(),
            new FormationVector(0, 0, 1),
            new FormationVector(1, 0, 0),
            new FormationVector(0, 1, 0)
        );
    }
}
