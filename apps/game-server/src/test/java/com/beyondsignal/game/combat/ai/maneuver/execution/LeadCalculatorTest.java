package com.beyondsignal.game.combat.ai.maneuver.execution;

import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import org.junit.jupiter.api.Test;

class LeadCalculatorTest {
    @Test
    void leadsMovingTarget() {
        LeadSolution solution = new LeadCalculator().calculate(
            CombatVector.zero(),
            200.0,
            new CombatVector(1000, 0, 0),
            new CombatVector(0, 50, 0)
        );

        assertTrue(solution.feasible());
        assertTrue(solution.aimPoint().y() > 0.0);
        assertTrue(solution.timeToImpact() > 0.0);
    }
}
