package com.beyondsignal.game.combat.ai.maneuver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ManeuverHistoryTest {
    @Test
    void preservesOrderedDecisionTimeline() {
        ManeuverHistory history = new ManeuverHistory();

        history.append(new ManeuverHistoryEntry(
            1L,
            decision(ManeuverType.ADVANCE),
            ManeuverState.PLANNED
        ));
        history.append(new ManeuverHistoryEntry(
            2L,
            decision(ManeuverType.ADVANCE),
            ManeuverState.EXECUTING
        ));

        assertEquals(2, history.entries().size());
        assertEquals(ManeuverState.EXECUTING, history.latest().state());

        assertThrows(
            IllegalArgumentException.class,
            () -> history.append(new ManeuverHistoryEntry(
                1L,
                decision(ManeuverType.WITHDRAW),
                ManeuverState.PLANNED
            ))
        );
    }

    private static ManeuverDecision decision(ManeuverType type) {
        ManeuverScore score = new ManeuverScore(
            type,
            10,
            Map.of("test", 10)
        );
        return new ManeuverDecision(
            type,
            ManeuverObjective.CLOSE_RANGE,
            List.of(score),
            75
        );
    }
}
