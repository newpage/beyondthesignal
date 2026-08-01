package com.beyondsignal.game.combat.ai.maneuver.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class KitingPlannerTest {
    @Test
    void retreatsWhenTargetIsTooClose() {
        KitingPlan plan = new KitingPlanner().plan(new KitingContext(
            UUID.randomUUID(),
            UUID.randomUUID(),
            CombatVector.zero(),
            new CombatVector(200, 0, 0),
            new CombatVector(-20, 0, 0),
            500.0,
            25.0,
            100.0
        ));

        assertEquals(RangeControlState.TOO_CLOSE, plan.state());
        assertTrue(plan.desiredVelocity().x() < 0.0);
    }

    @Test
    void matchesTargetVelocityInsideRangeBand() {
        CombatVector targetVelocity = new CombatVector(15, 5, 0);

        KitingPlan plan = new KitingPlanner().plan(new KitingContext(
            UUID.randomUUID(),
            UUID.randomUUID(),
            CombatVector.zero(),
            new CombatVector(500, 0, 0),
            targetVelocity,
            500.0,
            25.0,
            100.0
        ));

        assertEquals(RangeControlState.IN_BAND, plan.state());
        assertEquals(targetVelocity, plan.desiredVelocity());
    }
}
