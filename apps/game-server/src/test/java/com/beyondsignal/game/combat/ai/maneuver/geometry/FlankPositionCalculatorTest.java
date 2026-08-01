package com.beyondsignal.game.combat.ai.maneuver.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class FlankPositionCalculatorTest {
    @Test
    void calculatesLeftAndRightFlankPositions() {
        FlankGeometryContext context = new FlankGeometryContext(
            new CombatVector(0, 0, -1000),
            CombatVector.zero(),
            new CombatVector(0, 0, 1),
            new CombatVector(1, 0, 0),
            500.0
        );

        FlankPositionCalculator calculator = new FlankPositionCalculator();
        FlankSolution left = calculator.calculate(context, FlankSide.LEFT);
        FlankSolution right = calculator.calculate(context, FlankSide.RIGHT);

        assertEquals(new CombatVector(-500, 0, 0), left.destination());
        assertEquals(new CombatVector(500, 0, 0), right.destination());
        assertTrue(left.travelDistance() > 0.0);
        assertTrue(right.travelDistance() > 0.0);
    }
}
