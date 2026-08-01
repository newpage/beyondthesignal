package com.beyondsignal.game.combat.ai.maneuver.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import org.junit.jupiter.api.Test;

class HeadingControllerTest {
    @Test
    void limitsTurnRate() {
        HeadingController controller = new HeadingController();
        CombatVector result = controller.turnToward(
            new CombatVector(1, 0, 0),
            new CombatVector(0, 0, 1),
            15.0
        );

        double remaining = controller.headingErrorDegrees(
            result,
            new CombatVector(0, 0, 1)
        );

        assertTrue(remaining < 90.0);
        assertTrue(remaining > 60.0);
        assertEquals(1.0, result.magnitude(), 0.000001);
    }
}
