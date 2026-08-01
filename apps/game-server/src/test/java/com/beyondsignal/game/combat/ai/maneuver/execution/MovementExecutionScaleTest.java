package com.beyondsignal.game.combat.ai.maneuver.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import org.junit.jupiter.api.Test;

class MovementExecutionScaleTest {
    @Test
    void executesDeterministicallyForSixtyShips() {
        MovementExecutionEngine engine = new MovementExecutionEngine();
        List<MovementExecutionStep> first = new ArrayList<>();
        List<MovementExecutionStep> second = new ArrayList<>();

        for (int index = 0; index < 60; index++) {
            MovementExecutionContext context = new MovementExecutionContext(
                UUID.nameUUIDFromBytes(("ship-" + index).getBytes()),
                10L,
                new CombatVector(index, 0, 0),
                new CombatVector(index % 5, 0, 0),
                new CombatVector(1, 0, 0),
                new CombatVector(1000 + index * 10.0, index, 500),
                new CombatVector(80, 0, 20),
                new MovementLimits(200, 8, 12, 5, 2)
            );
            first.add(engine.execute(context));
            second.add(engine.execute(context));
        }

        assertEquals(first, second);
        assertEquals(60, first.size());
    }
}
