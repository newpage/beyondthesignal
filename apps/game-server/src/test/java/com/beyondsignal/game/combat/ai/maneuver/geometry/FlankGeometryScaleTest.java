package com.beyondsignal.game.combat.ai.maneuver.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class FlankGeometryScaleTest {
    @Test
    void plansDeterministicallyForThirtyShips() {
        GeometryAwareManeuverPlanner planner =
            new GeometryAwareManeuverPlanner();

        List<GeometryAwareManeuverRecommendation> first =
            new ArrayList<>();
        List<GeometryAwareManeuverRecommendation> second =
            new ArrayList<>();

        for (int index = 0; index < 30; index++) {
            FlankGeometryContext context = new FlankGeometryContext(
                new CombatVector(index * 25.0, 0, -1000 - index * 10.0),
                CombatVector.zero(),
                new CombatVector(0, 0, 1),
                new CombatVector(1, 0, 0),
                500.0 + index
            );

            first.add(planner.recommendFlank(context, 0.7, 0.85));
            second.add(planner.recommendFlank(context, 0.7, 0.85));
        }

        assertEquals(first, second);
        assertEquals(30, first.size());
    }
}
