package com.beyondsignal.game.combat.ai.maneuver.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrbitPlannerTest {
    @Test
    void establishesOrbitAtDesiredRadius() {
        OrbitPlan plan = new OrbitPlanner().plan(
            new OrbitContext(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new CombatVector(500, 0, 0),
                new CombatVector(0, 0, 60),
                CombatVector.zero(),
                new CombatVector(0, 1, 0),
                500.0,
                20.0,
                60.0
            ),
            OrbitDirection.COUNTERCLOCKWISE
        );

        assertEquals(OrbitState.ORBITING, plan.state());
        assertEquals(0.0, plan.radiusError(), 0.000001);
        assertTrue(plan.desiredVelocity().magnitude() >= 60.0);
    }

    @Test
    void correctsWhenTooClose() {
        OrbitPlan plan = new OrbitPlanner().plan(
            new OrbitContext(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new CombatVector(300, 0, 0),
                CombatVector.zero(),
                CombatVector.zero(),
                new CombatVector(0, 1, 0),
                500.0,
                20.0,
                60.0
            ),
            OrbitDirection.CLOCKWISE
        );

        assertEquals(OrbitState.CORRECTING_RANGE, plan.state());
        assertTrue(plan.radiusError() < 0.0);
    }
}
