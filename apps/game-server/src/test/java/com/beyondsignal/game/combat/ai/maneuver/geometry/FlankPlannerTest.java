package com.beyondsignal.game.combat.ai.maneuver.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import org.junit.jupiter.api.Test;

class FlankPlannerTest {
    @Test
    void selectsDeterministicallyWhenScoresTie() {
        FlankGeometryContext context = new FlankGeometryContext(
            new CombatVector(0, 0, -1000),
            CombatVector.zero(),
            new CombatVector(0, 0, 1),
            new CombatVector(1, 0, 0),
            500.0
        );

        FlankPlanner planner = new FlankPlanner();
        FlankSolution first = planner.plan(context);
        FlankSolution second = planner.plan(context);

        assertEquals(first, second);
        assertEquals(FlankSide.LEFT, first.side());

        List<FlankSolution> candidates = planner.candidates(context);
        assertEquals(2, candidates.size());
    }
}
