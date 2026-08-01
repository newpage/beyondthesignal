package com.beyondsignal.game.combat.ai.fleet.formation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.ai.fleet.FleetRole;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FormationUpdateProcessorTest {
    @Test
    void producesRecoveryObjectiveAndMovementIntents() {
        UUID fleetId = UUID.randomUUID();
        UUID leader = UUID.randomUUID();
        UUID escort = UUID.randomUUID();

        FormationAssignment leaderAssignment = assignment(
            leader,
            0,
            FormationVector.zero(),
            FleetRole.COMMANDER
        );
        FormationAssignment escortAssignment = assignment(
            escort,
            1,
            new FormationVector(100, 0, -100),
            FleetRole.ESCORT
        );

        FormationUpdate update = new FormationUpdateProcessor().process(
            fleetId,
            FormationType.WEDGE,
            List.of(leaderAssignment, escortAssignment),
            List.of(
                new FormationMemberState(
                    leader,
                    new FormationVector(20, 0, 0),
                    FormationVector.zero(),
                    5.0
                ),
                new FormationMemberState(
                    escort,
                    FormationVector.zero(),
                    escortAssignment.desiredPosition(),
                    5.0
                )
            )
        );

        assertEquals(
            FormationObjectiveType.RECOVER_FORMATION,
            update.objective().type()
        );
        assertEquals(2, update.movementIntents().size());
        assertTrue(update.recoveryPlan().recoveryRequired());
    }

    private static FormationAssignment assignment(
        UUID participantId,
        int index,
        FormationVector desired,
        FleetRole role
    ) {
        return new FormationAssignment(
            participantId,
            new FormationSlot(
                index,
                "slot-" + index,
                FormationVector.zero(),
                role,
                100 - index
            ),
            desired
        );
    }
}
