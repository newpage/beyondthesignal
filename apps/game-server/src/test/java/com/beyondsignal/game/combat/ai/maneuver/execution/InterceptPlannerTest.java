package com.beyondsignal.game.combat.ai.maneuver.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InterceptPlannerTest {
    @Test
    void createsPredictiveInterceptPlan() {
        PursuitPlan plan = new InterceptPlanner().plan(new PursuitContext(
            UUID.randomUUID(),
            UUID.randomUUID(),
            CombatVector.zero(),
            CombatVector.zero(),
            150.0,
            new CombatVector(1000, 0, 0),
            new CombatVector(20, 10, 0),
            250.0
        ));

        assertEquals(PursuitState.INTERCEPTING, plan.state());
        assertTrue(plan.interceptPoint().x() > 1000.0);
        assertTrue(plan.desiredClosingSpeed() > 0.0);
        assertTrue(plan.confidence() >= 50);
    }

    @Test
    void matchesVelocityInsideDesiredRange() {
        PursuitPlan plan = new InterceptPlanner().plan(new PursuitContext(
            UUID.randomUUID(),
            UUID.randomUUID(),
            new CombatVector(900, 0, 0),
            CombatVector.zero(),
            150.0,
            new CombatVector(1000, 0, 0),
            new CombatVector(10, 0, 0),
            250.0
        ));

        assertEquals(PursuitState.MATCHING_VELOCITY, plan.state());
    }
}
