package com.beyondsignal.game.combat.ai.fleet.formation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.beyondsignal.game.combat.ai.fleet.FleetMember;
import com.beyondsignal.game.combat.ai.fleet.FleetRole;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FormationAllocatorTest {
    @Test
    void assignsRoleCompatibleSlotsDeterministically() {
        UUID commander = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID escort = UUID.fromString("00000000-0000-0000-0000-000000000002");
        UUID strike = UUID.fromString("00000000-0000-0000-0000-000000000003");

        FormationTemplate template =
            FormationTemplates.create(FormationType.WEDGE, 100.0);
        FormationAnchor anchor = anchor(commander);

        List<FormationAssignment> assignments = new FormationAllocator().allocate(
            List.of(
                new FleetMember(strike, FleetRole.STRIKE, 50),
                new FleetMember(commander, FleetRole.COMMANDER, 100),
                new FleetMember(escort, FleetRole.ESCORT, 75)
            ),
            template,
            anchor
        );

        assertEquals(3, assignments.size());

        FormationAssignment commanderAssignment = assignmentFor(
            assignments,
            commander
        );
        FormationAssignment escortAssignment = assignmentFor(
            assignments,
            escort
        );
        FormationAssignment strikeAssignment = assignmentFor(
            assignments,
            strike
        );

        assertEquals("leader", commanderAssignment.slot().name());
        assertEquals(
            FleetRole.ESCORT,
            escortAssignment.slot().preferredRole()
        );
        assertEquals(
            FleetRole.STRIKE,
            strikeAssignment.slot().preferredRole()
        );
    }

    private static FormationAssignment assignmentFor(
        List<FormationAssignment> assignments,
        UUID participantId
    ) {
        return assignments.stream()
            .filter(assignment -> assignment.participantId().equals(participantId))
            .findFirst()
            .orElseThrow();
    }

    private static FormationAnchor anchor(UUID leader) {
        return new FormationAnchor(
            leader,
            FormationVector.zero(),
            new FormationVector(0, 0, 1),
            new FormationVector(1, 0, 0),
            new FormationVector(0, 1, 0)
        );
    }
}
