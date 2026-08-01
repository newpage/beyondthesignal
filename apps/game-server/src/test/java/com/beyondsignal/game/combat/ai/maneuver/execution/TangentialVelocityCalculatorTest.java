package com.beyondsignal.game.combat.ai.maneuver.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import org.junit.jupiter.api.Test;

class TangentialVelocityCalculatorTest {
    @Test
    void producesOppositeTangentsForOrbitDirections() {
        TangentialVelocityCalculator calculator =
            new TangentialVelocityCalculator();

        CombatVector counterclockwise = calculator.calculate(
            new CombatVector(1, 0, 0),
            new CombatVector(0, 1, 0),
            OrbitDirection.COUNTERCLOCKWISE,
            50.0
        );

        CombatVector clockwise = calculator.calculate(
            new CombatVector(1, 0, 0),
            new CombatVector(0, 1, 0),
            OrbitDirection.CLOCKWISE,
            50.0
        );

        assertEquals(counterclockwise.scale(-1.0), clockwise);
        assertEquals(50.0, counterclockwise.magnitude(), 0.000001);
    }
}
