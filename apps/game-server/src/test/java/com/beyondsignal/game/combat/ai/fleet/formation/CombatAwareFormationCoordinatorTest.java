package com.beyondsignal.game.combat.ai.fleet.formation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.ai.fleet.FleetDefinition;
import com.beyondsignal.game.combat.ai.fleet.FleetDoctrine;
import com.beyondsignal.game.combat.ai.fleet.FleetMember;
import com.beyondsignal.game.combat.ai.fleet.FleetRole;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatAwareFormationCoordinatorTest {
    @Test
    void coordinatesCasualtyRecoveryAndCommanderProtection() {
        UUID fleetId = UUID.randomUUID();
        UUID commander = UUID.randomUUID();
        UUID escort = UUID.randomUUID();
        UUID lostStrike = UUID.randomUUID();

        FleetDefinition fleet = new FleetDefinition(
            fleetId,
            FleetDoctrine.DEFENSIVE_SCREEN,
            List.of(
                new FleetMember(commander, FleetRole.COMMANDER, 100),
                new FleetMember(escort, FleetRole.ESCORT, 80),
                new FleetMember(lostStrike, FleetRole.STRIKE, 60)
            )
        );

        FormationPlan initialPlan = new FormationPlanner().plan(
            fleet,
            FormationTemplates.create(FormationType.WEDGE, 100.0),
            anchor(commander)
        );

        List<FormationMemberState> memberStates = initialPlan.assignments().stream()
            .filter(value -> !value.participantId().equals(lostStrike))
            .map(value -> new FormationMemberState(
                value.participantId(),
                value.desiredPosition(),
                value.desiredPosition(),
                5.0
            ))
            .toList();

        List<FormationMemberCombatState> combatStates = List.of(
            new FormationMemberCombatState(
                commander,
                0.80,
                0.70,
                true,
                true,
                FormationCombatState.MAINTAINING
            ),
            new FormationMemberCombatState(
                escort,
                0.90,
                0.90,
                false,
                false,
                FormationCombatState.MAINTAINING
            )
        );

        FormationLifecycleUpdate update =
            new CombatAwareFormationCoordinator().update(
                fleet,
                initialPlan,
                new FormationCasualtyUpdate(Set.of(lostStrike)),
                memberStates,
                combatStates,
                FormationCombatPolicy.standard(),
                true
            );

        assertEquals(2, update.activePlan().assignments().size());
        assertFalse(update.activePlan().assignments().stream()
            .anyMatch(value -> value.participantId().equals(lostStrike)));
        assertEquals(1, update.protectionOrders().size());
        assertEquals(escort, update.protectionOrders().getFirst().protectorId());
        assertTrue(update.combatDecisions().stream()
            .anyMatch(decision ->
                decision.state() == FormationCombatState.PROTECTING_COMMANDER));
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
