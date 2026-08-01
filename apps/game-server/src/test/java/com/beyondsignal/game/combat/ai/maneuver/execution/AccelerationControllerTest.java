package com.beyondsignal.game.combat.ai.maneuver.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class AccelerationControllerTest {
    @Test
    void clampsAccelerationAndDeceleration() {
        AccelerationController controller = new AccelerationController();

        assertEquals(10.0, controller.command(0.0, 100.0, 10.0));
        assertEquals(-10.0, controller.command(100.0, 0.0, 10.0));
        assertEquals(5.0, controller.command(40.0, 45.0, 10.0));
    }
}
