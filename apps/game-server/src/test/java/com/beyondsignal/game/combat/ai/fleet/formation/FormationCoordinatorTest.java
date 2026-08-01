package com.beyondsignal.game.combat.ai.fleet.formation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.ai.fleet.FleetDefinition;
import com.beyondsignal.game.combat.ai.fleet.FleetDoctrine;
import com.beyondsignal.game.combat.ai.fleet.FleetMember;
import com.beyondsignal.game.combat.ai.fleet.FleetRole;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FormationCoordinatorTest {
    @Test
    void initializesAndEvaluatesFormation() {
        UUID commander = UUID.randomUUID();
        UUID escort = UUID.randomUUID();

        FleetDefinition fleet = new FleetDefinition(
            UUID.randomUUID(),
            FleetDoctrine.FOCUS_FIRE,
            List.of(
                new FleetMember(commander, FleetRole.COMMANDER, 100),
                new FleetMember(escort, FleetRole.ESCORT, 80)
            )
        );

        FormationAnchor anchor = new FormationAnchor(
            commander,
            FormationVector.zero(),
            new FormationVector(0, 0, 1),
            new FormationVector(1, 0, 0),
            new FormationVector(0, 1, 0)
        );

        FormationCoordinator coordinator = new FormationCoordinator();
        FormationPlan plan = coordinator.initialize(
            fleet,
            FormationType.WEDGE,
            100.0,
            anchor
        );

        List<FormationMemberState> states = plan.assignments().stream()
            .map(assignment -> new FormationMemberState(
                assignment.participantId(),
                assignment.desiredPosition(),
                assignment.desiredPosition(),
                5.0
            ))
            .toList();

        FormationState state = coordinator.evaluate(
            fleet.fleetId(),
            FormationType.WEDGE,
            plan.assignments(),
            states
        );

        assertEquals(FormationStatus.STABLE, state.status());
        assertTrue(state.integrity() >= 0.85);
    }
}
