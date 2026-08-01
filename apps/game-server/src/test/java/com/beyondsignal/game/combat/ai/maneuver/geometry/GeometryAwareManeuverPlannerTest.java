package com.beyondsignal.game.combat.ai.maneuver.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.ai.maneuver.ManeuverType;
import org.junit.jupiter.api.Test;

class GeometryAwareManeuverPlannerTest {
    @Test
    void returnsDeterministicFlankRecommendation() {
        FlankGeometryContext context = new FlankGeometryContext(
            new CombatVector(0, 0, -1500),
            CombatVector.zero(),
            new CombatVector(0, 0, 1),
            new CombatVector(1, 0, 0),
            600.0
        );

        GeometryAwareManeuverPlanner planner =
            new GeometryAwareManeuverPlanner();

        GeometryAwareManeuverRecommendation first =
            planner.recommendFlank(context, 0.8, 0.9);
        GeometryAwareManeuverRecommendation second =
            planner.recommendFlank(context, 0.8, 0.9);

        assertEquals(first, second);
        assertEquals(ManeuverType.FLANK_LEFT, first.maneuver());
        assertTrue(first.geometryScore() > 0);
    }
}
