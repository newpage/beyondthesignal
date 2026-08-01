package com.beyondsignal.game.combat.ai.tactical;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TacticalTargetScorerTest {
    @Test
    void flagshipScoresAboveScout() {
        TacticalTargetScorer scorer = new TacticalTargetScorer();

        TacticalTargetScore flagship = scorer.score(new TacticalTargetProfile(
            UUID.randomUUID(),
            TargetPriorityClass.FLAGSHIP,
            1.0,
            1.0,
            20,
            true,
            true,
            false
        ));

        TacticalTargetScore scout = scorer.score(new TacticalTargetProfile(
            UUID.randomUUID(),
            TargetPriorityClass.SCOUT,
            1.0,
            1.0,
            20,
            false,
            false,
            false
        ));

        assertTrue(flagship.score() > scout.score());
    }
}
