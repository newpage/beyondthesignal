package com.beyondsignal.game.combat.ai.maneuver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ManeuverSelectionIntegrationTest {
    @Test
    void plansDeterministicallyForTwentyShips() {
        ManeuverPlanner planner = new ManeuverPlanner();
        ManeuverSelectionPolicy policy = ManeuverSelectionPolicy.balanced();

        List<ManeuverDecision> first = new ArrayList<>();
        List<ManeuverDecision> second = new ArrayList<>();

        for (int index = 0; index < 20; index++) {
            ManeuverContext context = new ManeuverContext(
                UUID.nameUUIDFromBytes(("ship-" + index).getBytes()),
                UUID.nameUUIDFromBytes(("target-" + index).getBytes()),
                500.0 + index * 100.0,
                1000.0,
                1.0 - index * 0.02,
                1.0 - index * 0.015,
                0.90,
                index / 25.0,
                0.70,
                index % 2 == 0
                    ? ManeuverObjective.CLOSE_RANGE
                    : ManeuverObjective.GAIN_POSITIONAL_ADVANTAGE,
                ManeuverConstraints.unrestricted()
            );

            first.add(planner.plan(context, policy));
            second.add(planner.plan(context, policy));
        }

        assertEquals(first, second);
        assertEquals(20, first.size());
    }
}
