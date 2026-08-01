package com.beyondsignal.game.combat.ai.maneuver.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import org.junit.jupiter.api.Test;

class PredictivePursuitIntegrationTest {
    @Test
    void producesDeterministicPlansForFortyPursuers() {
        InterceptPlanner planner = new InterceptPlanner();

        List<PursuitPlan> first = new ArrayList<>();
        List<PursuitPlan> second = new ArrayList<>();

        for (int index = 0; index < 40; index++) {
            PursuitContext context = new PursuitContext(
                UUID.nameUUIDFromBytes(("pursuer-" + index).getBytes()),
                UUID.nameUUIDFromBytes(("target-" + index).getBytes()),
                new CombatVector(index * 10.0, 0, 0),
                CombatVector.zero(),
                100.0 + index,
                new CombatVector(1000 + index * 20.0, index * 5.0, 0),
                new CombatVector(10 + index * 0.2, 2, 0),
                200.0
            );

            first.add(planner.plan(context));
            second.add(planner.plan(context));
        }

        assertEquals(first, second);
        assertEquals(40, first.size());
    }
}
