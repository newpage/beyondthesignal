package com.beyondsignal.game.combat.ai.maneuver.geometry;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ApproachVectorEvaluatorTest {
    @Test
    void rearApproachScoresAboveForwardApproach() {
        ApproachVectorEvaluator evaluator = new ApproachVectorEvaluator();
        CombatVector targetForward = new CombatVector(0, 0, 1);
        CombatVector targetRight = new CombatVector(1, 0, 0);

        ApproachVectorScore rear = evaluator.evaluate(
            new CombatVector(0, 0, 1),
            targetForward,
            targetRight,
            0.8,
            0.9
        );

        ApproachVectorScore forward = evaluator.evaluate(
            new CombatVector(0, 0, -1),
            targetForward,
            targetRight,
            0.8,
            0.9
        );

        assertTrue(rear.score() > forward.score());
    }
}
