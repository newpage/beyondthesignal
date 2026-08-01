package com.beyondsignal.game.combat.ai.fleet.formation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FormationMemberStateTest {
    @Test
    void detectsWhetherMemberIsWithinTolerance() {
        FormationMemberState stable = new FormationMemberState(
            UUID.randomUUID(),
            new FormationVector(10, 0, 0),
            new FormationVector(12, 0, 0),
            3.0
        );

        FormationMemberState drifting = new FormationMemberState(
            UUID.randomUUID(),
            new FormationVector(10, 0, 0),
            new FormationVector(20, 0, 0),
            3.0
        );

        assertTrue(stable.inPosition());
        assertFalse(drifting.inPosition());
    }
}
