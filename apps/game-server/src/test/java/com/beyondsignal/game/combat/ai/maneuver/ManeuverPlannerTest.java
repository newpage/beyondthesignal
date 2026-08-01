package com.beyondsignal.game.combat.ai.maneuver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ManeuverPlannerTest {
    @Test
    void selectsDeterministicManeuver() {
        ManeuverContext context = new ManeuverContext(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            UUID.fromString("00000000-0000-0000-0000-000000000002"),
            2200.0,
            1000.0,
            1.0,
            1.0,
            0.95,
            0.10,
            0.80,
            ManeuverObjective.CLOSE_RANGE,
            ManeuverConstraints.unrestricted()
        );

        ManeuverPlanner planner = new ManeuverPlanner();
        ManeuverDecision first = planner.plan(
            context,
            ManeuverSelectionPolicy.balanced()
        );
        ManeuverDecision second = planner.plan(
            context,
            ManeuverSelectionPolicy.balanced()
        );

        assertEquals(first, second);
        assertEquals(ManeuverType.ADVANCE, first.selected());
        assertTrue(first.confidence() >= 50);
    }

    @Test
    void respectsProhibitedManeuver() {
        ManeuverContext context = new ManeuverContext(
            UUID.randomUUID(),
            UUID.randomUUID(),
            500.0,
            1000.0,
            0.20,
            0.20,
            0.90,
            0.90,
            0.20,
            ManeuverObjective.DISENGAGE,
            new ManeuverConstraints(
                java.util.Set.of(ManeuverType.WITHDRAW),
                false,
                false,
                false
            )
        );

        ManeuverDecision decision = new ManeuverPlanner().plan(
            context,
            ManeuverSelectionPolicy.balanced()
        );

        assertTrue(decision.selected() != ManeuverType.WITHDRAW);
    }
}
