package com.beyondsignal.game.combat.ai.maneuver.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class RelativeHeadingAnalyzerTest {
    @Test
    void classifiesForwardRearAndFlanks() {
        RelativeHeadingAnalyzer analyzer = new RelativeHeadingAnalyzer();
        CombatVector forward = new CombatVector(0, 0, 1);
        CombatVector right = new CombatVector(1, 0, 0);

        assertEquals(
            AttackArc.FORWARD,
            analyzer.analyze(
                forward,
                new CombatVector(0, 0, 1),
                right
            ).arc()
        );
        assertEquals(
            AttackArc.REAR,
            analyzer.analyze(
                forward,
                new CombatVector(0, 0, -1),
                right
            ).arc()
        );
        assertEquals(
            AttackArc.RIGHT_FLANK,
            analyzer.analyze(
                forward,
                new CombatVector(1, 0, 0),
                right
            ).arc()
        );
        assertEquals(
            AttackArc.LEFT_FLANK,
            analyzer.analyze(
                forward,
                new CombatVector(-1, 0, 0),
                right
            ).arc()
        );
    }
}
