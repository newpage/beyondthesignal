package com.beyondsignal.game.combat.ai.maneuver;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ManeuverScorerTest {
    @Test
    void closeRangeObjectivePrefersAdvanceOverWithdraw() {
        ManeuverContext context = context(
            2.0,
            ManeuverObjective.CLOSE_RANGE,
            1.0,
            1.0
        );
        ManeuverSelectionPolicy policy = ManeuverSelectionPolicy.balanced();
        ManeuverScorer scorer = new ManeuverScorer();

        ManeuverScore advance = scorer.score(
            ManeuverType.ADVANCE,
            context,
            policy
        );
        ManeuverScore withdraw = scorer.score(
            ManeuverType.WITHDRAW,
            context,
            policy
        );

        assertTrue(advance.total() > withdraw.total());
    }

    @Test
    void criticalConditionPrefersWithdraw() {
        ManeuverContext context = new ManeuverContext(
            UUID.randomUUID(),
            UUID.randomUUID(),
            1000.0,
            1000.0,
            0.10,
            0.05,
            0.80,
            0.95,
            0.20,
            ManeuverObjective.DISENGAGE,
            ManeuverConstraints.unrestricted()
        );

        ManeuverScorer scorer = new ManeuverScorer();
        ManeuverSelectionPolicy policy = ManeuverSelectionPolicy.balanced();

        assertTrue(
            scorer.score(ManeuverType.WITHDRAW, context, policy).total()
                > scorer.score(ManeuverType.ADVANCE, context, policy).total()
        );
    }

    private static ManeuverContext context(
        double normalizedDistance,
        ManeuverObjective objective,
        double hull,
        double shields
    ) {
        return new ManeuverContext(
            UUID.randomUUID(),
            UUID.randomUUID(),
            normalizedDistance * 1000.0,
            1000.0,
            hull,
            shields,
            0.90,
            0.20,
            0.70,
            objective,
            ManeuverConstraints.unrestricted()
        );
    }
}
