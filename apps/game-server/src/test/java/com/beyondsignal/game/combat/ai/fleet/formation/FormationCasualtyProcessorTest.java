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

class FormationCasualtyProcessorTest {
    @Test
    void removesLostShipAndReassignsRemainingMembers() {
        UUID commander = UUID.randomUUID();
        UUID lostEscort = UUID.randomUUID();
        UUID strike = UUID.randomUUID();

        FleetDefinition fleet = new FleetDefinition(
            UUID.randomUUID(),
            FleetDoctrine.BALANCED,
            List.of(
                new FleetMember(commander, FleetRole.COMMANDER, 100),
                new FleetMember(lostEscort, FleetRole.ESCORT, 80),
                new FleetMember(strike, FleetRole.STRIKE, 60)
            )
        );

        FormationTemplate template =
            FormationTemplates.create(FormationType.DIAMOND, 100.0);

        FormationPlan result = new FormationCasualtyProcessor().process(
            fleet,
            new FormationCasualtyUpdate(Set.of(lostEscort)),
            template,
            anchor(commander)
        );

        assertEquals(2, result.assignments().size());
        assertFalse(result.assignments().stream()
            .anyMatch(value -> value.participantId().equals(lostEscort)));
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
