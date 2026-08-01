package com.beyondsignal.game.combat.ai.maneuver.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrbitControllerTest {
    @Test
    void retainsPlanForSmallTargetMovement() {
        UUID participant = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        OrbitController controller = new OrbitController();

        OrbitPlan first = controller.update(
            context(participant, target, CombatVector.zero()),
            OrbitDirection.COUNTERCLOCKWISE
        );

        OrbitPlan second = controller.update(
            context(participant, target, new CombatVector(5, 0, 0)),
            OrbitDirection.COUNTERCLOCKWISE
        );

        assertEquals(first, second);

        OrbitPlan changed = controller.update(
            context(participant, target, new CombatVector(200, 0, 0)),
            OrbitDirection.COUNTERCLOCKWISE
        );

        assertNotEquals(first.desiredPosition(), changed.desiredPosition());
    }

    private static OrbitContext context(
        UUID participant,
        UUID target,
        CombatVector targetPosition
    ) {
        return new OrbitContext(
            participant,
            target,
            new CombatVector(500, 0, 0),
            new CombatVector(0, 0, 60),
            targetPosition,
            new CombatVector(0, 1, 0),
            500.0,
            20.0,
            60.0
        );
    }
}
