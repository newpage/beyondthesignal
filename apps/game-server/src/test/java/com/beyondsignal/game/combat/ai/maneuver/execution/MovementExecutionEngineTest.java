package com.beyondsignal.game.combat.ai.maneuver.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MovementExecutionEngineTest {
    @Test
    void turnsBeforeAcceleratingTowardDestination() {
        MovementExecutionStep step = new MovementExecutionEngine().execute(
            context(
                new CombatVector(1, 0, 0),
                new CombatVector(0, 0, 1000),
                CombatVector.zero(),
                new CombatVector(0, 0, 100)
            )
        );

        assertEquals(MovementExecutionState.TURNING, step.state());
        assertTrue(step.command().desiredHeading().z() > 0.0);
    }

    @Test
    void completesInsidePositionAndVelocityTolerance() {
        MovementExecutionStep step = new MovementExecutionEngine().execute(
            context(
                new CombatVector(0, 0, 1),
                new CombatVector(2, 0, 0),
                new CombatVector(10, 0, 0),
                new CombatVector(10, 0, 0)
            )
        );

        assertEquals(MovementExecutionState.COMPLETED, step.state());
    }

    private static MovementExecutionContext context(
        CombatVector heading,
        CombatVector desiredPosition,
        CombatVector currentVelocity,
        CombatVector desiredVelocity
    ) {
        return new MovementExecutionContext(
            UUID.randomUUID(),
            1L,
            CombatVector.zero(),
            currentVelocity,
            heading,
            desiredPosition,
            desiredVelocity,
            new MovementLimits(200, 10, 15, 5, 2)
        );
    }
}
