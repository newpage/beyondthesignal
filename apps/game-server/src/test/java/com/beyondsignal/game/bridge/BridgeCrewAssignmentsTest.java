package com.beyondsignal.game.bridge;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BridgeCrewAssignmentsTest {
    @Test
    void playerCanOccupyOnlyOneStation() {
        BridgeCrewAssignments assignments = new BridgeCrewAssignments();
        UUID player = UUID.randomUUID();

        assignments.assign(BridgeStation.HELM, player);
        assignments.assign(BridgeStation.SCIENCE, player);

        assertFalse(assignments.occupant(BridgeStation.HELM).isPresent());
        assertTrue(assignments.authorized(BridgeStation.SCIENCE, player));
    }
}
