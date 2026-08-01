package com.beyondsignal.game.combat.ai.explain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.beyondsignal.game.combat.ai.goal.CombatAiGoalType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatAiDecisionTimelineTest {
    @Test
    void preservesDecisionOrderAndRejectsOtherParticipant() {
        UUID participantId = UUID.randomUUID();
        CombatAiDecisionTimeline timeline =
            new CombatAiDecisionTimeline(participantId);

        timeline.append(record(participantId, 1L));
        timeline.append(record(participantId, 2L));

        assertEquals(2, timeline.decisions().size());
        assertEquals(2L, timeline.latest().tick());

        assertThrows(
            IllegalArgumentException.class,
            () -> timeline.append(record(UUID.randomUUID(), 3L))
        );
    }

    private static CombatAiDecisionRecord record(UUID participantId, long tick) {
        return new CombatAiDecisionRecord(
            participantId,
            tick,
            CombatAiGoalType.HOLD_POSITION,
            null,
            new DecisionConfidence(0.5),
            List.of(),
            List.of()
        );
    }
}
