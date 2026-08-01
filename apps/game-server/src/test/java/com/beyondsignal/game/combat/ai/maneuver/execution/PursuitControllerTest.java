package com.beyondsignal.game.combat.ai.maneuver.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PursuitControllerTest {
    @Test
    void retainsPlanUntilTargetMovesBeyondThreshold() {
        UUID pursuer = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        PursuitController controller = new PursuitController();

        PursuitPlan first = controller.update(context(
            pursuer,
            target,
            new CombatVector(1000, 0, 0)
        ));

        PursuitPlan second = controller.update(context(
            pursuer,
            target,
            new CombatVector(1005, 0, 0)
        ));

        assertEquals(first, second);
        assertTrue(controller.hasActivePlan());

        PursuitPlan replanned = controller.update(context(
            pursuer,
            target,
            new CombatVector(1400, 0, 0)
        ));

        assertTrue(
            replanned.interceptPoint().distanceTo(first.interceptPoint()) > 25.0
        );
    }

    private static PursuitContext context(
        UUID pursuer,
        UUID target,
        CombatVector targetPosition
    ) {
        return new PursuitContext(
            pursuer,
            target,
            CombatVector.zero(),
            CombatVector.zero(),
            150.0,
            targetPosition,
            new CombatVector(15, 0, 0),
            200.0
        );
    }
}
