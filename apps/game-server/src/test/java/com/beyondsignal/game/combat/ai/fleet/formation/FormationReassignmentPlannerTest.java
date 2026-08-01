package com.beyondsignal.game.combat.ai.fleet.formation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import com.beyondsignal.game.combat.ai.fleet.FleetDefinition;
import com.beyondsignal.game.combat.ai.fleet.FleetDoctrine;
import com.beyondsignal.game.combat.ai.fleet.FleetMember;
import com.beyondsignal.game.combat.ai.fleet.FleetRole;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FormationReassignmentPlannerTest {
    @Test
    void removesLostMemberAndCompactsAssignments() {
        UUID commander = UUID.randomUUID();
        UUID lostEscort = UUID.randomUUID();
        UUID survivor = UUID.randomUUID();

        FleetDefinition fleet = new FleetDefinition(
            UUID.randomUUID(),
            FleetDoctrine.BALANCED,
            List.of(
                new FleetMember(commander, FleetRole.COMMANDER, 100),
                new FleetMember(lostEscort, FleetRole.ESCORT, 80),
                new FleetMember(survivor, FleetRole.STRIKE, 60)
            )
        );

        FormationTemplate template =
            FormationTemplates.create(FormationType.DIAMOND, 100.0);
        FormationAnchor anchor = new FormationAnchor(
            commander,
            FormationVector.zero(),
            new FormationVector(0, 0, 1),
            new FormationVector(1, 0, 0),
            new FormationVector(0, 1, 0)
        );

        FormationPlan plan = new FormationReassignmentPlanner().reassignAfterLoss(
            fleet,
            Set.of(lostEscort),
            template,
            anchor
        );

        assertEquals(2, plan.assignments().size());
        assertFalse(plan.assignments().stream()
            .anyMatch(value -> value.participantId().equals(lostEscort)));
    }
}
