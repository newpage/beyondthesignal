package com.beyondsignal.game.combat.ai.maneuver.geometry;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class InterceptionCalculatorTest {
    @Test
    void calculatesFeasibleIntercept() {
        InterceptionSolution solution = new InterceptionCalculator().calculate(
            CombatVector.zero(),
            100.0,
            new CombatVector(1000, 0, 0),
            new CombatVector(10, 0, 0)
        );

        assertTrue(solution.feasible());
        assertTrue(solution.timeToIntercept() > 0.0);
        assertTrue(solution.interceptPoint().x() > 1000.0);
    }

    @Test
    void marksInterceptInfeasibleWhenTargetIsTooFast() {
        InterceptionSolution solution = new InterceptionCalculator().calculate(
            CombatVector.zero(),
            10.0,
            new CombatVector(1000, 0, 0),
            new CombatVector(100, 0, 0)
        );

        assertFalse(solution.feasible());
    }
}
