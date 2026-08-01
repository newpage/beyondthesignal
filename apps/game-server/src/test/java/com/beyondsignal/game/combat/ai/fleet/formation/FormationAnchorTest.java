package com.beyondsignal.game.combat.ai.fleet.formation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.beyondsignal.game.combat.ai.fleet.FleetRole;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FormationAnchorTest {
    @Test
    void transformsLocalSlotOffsetIntoWorldPosition() {
        FormationAnchor anchor = new FormationAnchor(
            UUID.randomUUID(),
            new FormationVector(100, 200, 300),
            new FormationVector(0, 0, 1),
            new FormationVector(1, 0, 0),
            new FormationVector(0, 1, 0)
        );

        FormationSlot slot = new FormationSlot(
            1,
            "left-rear",
            new FormationVector(-2, 1, -3),
            FleetRole.ESCORT,
            90
        );

        FormationVector position = anchor.worldPosition(slot, 10.0);

        assertEquals(new FormationVector(80, 210, 270), position);
    }
}
