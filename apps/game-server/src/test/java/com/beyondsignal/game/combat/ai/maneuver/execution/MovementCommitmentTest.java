package com.beyondsignal.game.combat.ai.maneuver.execution;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class MovementCommitmentTest {
    @Test
    void enforcesMinimumAndMaximumWindows() {
        MovementCommitment commitment = new MovementCommitment(100, 5, 20);

        assertFalse(commitment.mayReplan(104));
        assertTrue(commitment.mayReplan(105));
        assertFalse(commitment.mustReplan(119));
        assertTrue(commitment.mustReplan(120));
    }
}
