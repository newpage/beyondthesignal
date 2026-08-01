package com.beyondsignal.game.combat.ai.fleet.formation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.ai.fleet.FleetRole;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FormationCommandTranslatorTest {
    @Test
    void emitsHoldIntentWhenStableMemberIsInPosition() {
        UUID fleetId = UUID.randomUUID();
        UUID participantId = UUID.randomUUID();
        FormationSlot slot = new FormationSlot(
            0,
            "leader",
            FormationVector.zero(),
            FleetRole.COMMANDER,
            100
        );
        FormationAssignment assignment = new FormationAssignment(
            participantId,
            slot,
            FormationVector.zero()
        );
        FormationMemberState state = new FormationMemberState(
            participantId,
            FormationVector.zero(),
            FormationVector.zero(),
            5.0
        );
        FormationObjective objective = new FormationObjective(
            fleetId,
            FormationObjectiveType.MAINTAIN_FORMATION,
            FormationType.WEDGE,
            35,
            Map.of("formationStable", 35)
        );

        FormationMovementIntent intent =
            new FormationCommandTranslator().translate(
                assignment,
                state,
                objective
            );

        assertTrue(intent.holdPosition());
    }

    @Test
    void emitsMovementIntentWhenMemberHasDrifted() {
        UUID fleetId = UUID.randomUUID();
        UUID participantId = UUID.randomUUID();
        FormationSlot slot = new FormationSlot(
            1,
            "escort",
            new FormationVector(1, 0, -1),
            FleetRole.ESCORT,
            90
        );
        FormationAssignment assignment = new FormationAssignment(
            participantId,
            slot,
            new FormationVector(100, 0, -100)
        );
        FormationMemberState state = new FormationMemberState(
            participantId,
            FormationVector.zero(),
            assignment.desiredPosition(),
            5.0
        );
        FormationObjective objective = new FormationObjective(
            fleetId,
            FormationObjectiveType.RECOVER_FORMATION,
            FormationType.WEDGE,
            80,
            Map.of("formationDegraded", 80)
        );

        FormationMovementIntent intent =
            new FormationCommandTranslator().translate(
                assignment,
                state,
                objective
            );

        assertFalse(intent.holdPosition());
        assertTrue(intent.urgency() >= 80);
    }
}
