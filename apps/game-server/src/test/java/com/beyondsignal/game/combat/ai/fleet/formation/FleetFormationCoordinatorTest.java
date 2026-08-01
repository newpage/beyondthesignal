package com.beyondsignal.game.combat.ai.fleet.formation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.beyondsignal.game.combat.ai.fleet.FleetDefinition;
import com.beyondsignal.game.combat.ai.fleet.FleetDoctrine;
import com.beyondsignal.game.combat.ai.fleet.FleetMember;
import com.beyondsignal.game.combat.ai.fleet.FleetRole;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FleetFormationCoordinatorTest {
    @Test
    void establishesAndMaintainsFormation() {
        UUID fleetId = UUID.randomUUID();
        UUID commander = UUID.randomUUID();
        UUID escort = UUID.randomUUID();

        FleetDefinition fleet = new FleetDefinition(
            fleetId,
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

        FleetFormationCoordinator coordinator =
            new FleetFormationCoordinator();

        FormationPlan plan = coordinator.establish(
            fleet,
            FormationType.WEDGE,
            100.0,
            anchor
        );

        List<FormationMemberState> members = plan.assignments().stream()
            .map(assignment -> new FormationMemberState(
                assignment.participantId(),
                assignment.desiredPosition(),
                assignment.desiredPosition(),
                5.0
            ))
            .toList();

        FormationUpdate update = coordinator.update(
            fleetId,
            FormationType.WEDGE,
            plan.assignments(),
            members
        );

        assertEquals(FormationStatus.STABLE, update.state().status());
        assertEquals(
            FormationObjectiveType.MAINTAIN_FORMATION,
            update.objective().type()
        );
        assertEquals(2, update.orders().orders().size());
    }
}
