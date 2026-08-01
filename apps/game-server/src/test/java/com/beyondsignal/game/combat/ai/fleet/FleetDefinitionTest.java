package com.beyondsignal.game.combat.ai.fleet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FleetDefinitionTest {
    @Test
    void exposesSingleCommander() {
        UUID commanderId = UUID.randomUUID();
        FleetDefinition fleet = new FleetDefinition(
            UUID.randomUUID(),
            FleetDoctrine.FOCUS_FIRE,
            List.of(
                new FleetMember(commanderId, FleetRole.COMMANDER, 100),
                new FleetMember(UUID.randomUUID(), FleetRole.ESCORT, 50)
            )
        );

        assertEquals(commanderId, fleet.commander().orElseThrow().participantId());
    }

    @Test
    void rejectsMultipleCommanders() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new FleetDefinition(
                UUID.randomUUID(),
                FleetDoctrine.BALANCED,
                List.of(
                    new FleetMember(UUID.randomUUID(), FleetRole.COMMANDER, 100),
                    new FleetMember(UUID.randomUUID(), FleetRole.COMMANDER, 90)
                )
            )
        );
    }
}
