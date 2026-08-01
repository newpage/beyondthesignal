package com.beyondsignal.game.combat.ai.maneuver.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MovementExecutionSessionTest {
    @Test
    void recordsDeterministicExecutionTimeline() {
        MovementExecutionSession session = new MovementExecutionSession(
            new MovementExecutionEngine(),
            new MovementCommitment(0, 3, 10)
        );

        for (long tick = 0; tick < 5; tick++) {
            session.update(new MovementExecutionContext(
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                tick,
                CombatVector.zero(),
                CombatVector.zero(),
                new CombatVector(1, 0, 0),
                new CombatVector(0, 0, 1000),
                new CombatVector(0, 0, 100),
                new MovementLimits(200, 10, 15, 5, 2)
            ));
        }

        assertEquals(5, session.steps().size());
        assertTrue(session.mayReplan(3));
        assertTrue(session.steps().stream()
            .allMatch(step -> step.state() == MovementExecutionState.TURNING));
    }
}
