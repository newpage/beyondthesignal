package com.beyondsignal.game.combat.ai.fleet.formation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.beyondsignal.game.combat.ai.fleet.FleetRole;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FormationOrderGeneratorTest {
    @Test
    void sortsOrdersByUrgencyThenParticipantId() {
        UUID fleetId = UUID.randomUUID();
        UUID first = UUID.fromString(
            "00000000-0000-0000-0000-000000000001"
        );
        UUID second = UUID.fromString(
            "00000000-0000-0000-0000-000000000002"
        );

        FormationAssignment firstAssignment = assignment(
            first,
            0,
            FormationVector.zero()
        );
        FormationAssignment secondAssignment = assignment(
            second,
            1,
            new FormationVector(100, 0, 0)
        );

        FormationState state = new FormationState(
            fleetId,
            FormationType.LINE_ABREAST,
            FormationStatus.DEGRADED,
            List.of(firstAssignment, secondAssignment),
            List.of(
                new FormationMemberState(
                    first,
                    new FormationVector(10, 0, 0),
                    FormationVector.zero(),
                    5.0
                ),
                new FormationMemberState(
                    second,
                    FormationVector.zero(),
                    new FormationVector(100, 0, 0),
                    5.0
                )
            ),
            0.35
        );

        FormationObjective objective = new FormationObjective(
            fleetId,
            FormationObjectiveType.RECOVER_FORMATION,
            FormationType.LINE_ABREAST,
            80,
            Map.of("formationDegraded", 80)
        );

        FormationOrderSet orders = new FormationOrderGenerator().generate(
            state,
            objective
        );

        assertEquals(second, orders.orders().getFirst().participantId());
    }

    private static FormationAssignment assignment(
        UUID participantId,
        int index,
        FormationVector desired
    ) {
        return new FormationAssignment(
            participantId,
            new FormationSlot(
                index,
                "slot-" + index,
                FormationVector.zero(),
                index == 0 ? FleetRole.COMMANDER : FleetRole.ESCORT,
                100 - index
            ),
            desired
        );
    }
}
