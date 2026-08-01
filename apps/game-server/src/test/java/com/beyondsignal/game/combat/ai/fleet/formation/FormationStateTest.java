package com.beyondsignal.game.combat.ai.fleet.formation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.beyondsignal.game.combat.ai.fleet.FleetRole;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FormationStateTest {
    @Test
    void countsMembersInPositionAndFindsAssignment() {
        UUID fleetId = UUID.randomUUID();
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();

        FormationSlot firstSlot = new FormationSlot(
            0, "leader", FormationVector.zero(), FleetRole.COMMANDER, 100
        );
        FormationSlot secondSlot = new FormationSlot(
            1, "escort", new FormationVector(1, 0, -1), FleetRole.ESCORT, 90
        );

        FormationState state = new FormationState(
            fleetId,
            FormationType.WEDGE,
            FormationStatus.DEGRADED,
            List.of(
                new FormationAssignment(first, firstSlot, FormationVector.zero()),
                new FormationAssignment(
                    second,
                    secondSlot,
                    new FormationVector(10, 0, -10)
                )
            ),
            List.of(
                new FormationMemberState(
                    first,
                    FormationVector.zero(),
                    FormationVector.zero(),
                    1.0
                ),
                new FormationMemberState(
                    second,
                    FormationVector.zero(),
                    new FormationVector(10, 0, -10),
                    1.0
                )
            ),
            0.5
        );

        assertEquals(1, state.membersInPosition());
        assertEquals(
            secondSlot,
            state.assignmentFor(second).orElseThrow().slot()
        );
    }
}
