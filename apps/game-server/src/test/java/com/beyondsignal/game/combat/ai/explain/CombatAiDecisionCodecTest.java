package com.beyondsignal.game.combat.ai.explain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import com.beyondsignal.game.combat.ai.goal.CombatAiGoalType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatAiDecisionCodecTest {
    @Test
    void sameDecisionStreamProducesSameChecksum() {
        CombatAiDecisionCodec codec = new CombatAiDecisionCodec();
        UUID participantId = UUID.randomUUID();

        List<CombatAiDecisionRecord> decisions = List.of(
            record(participantId, 1L, 0.75),
            record(participantId, 2L, 0.80)
        );

        assertEquals(codec.checksum(decisions), codec.checksum(decisions));
        assertNotEquals(
            codec.checksum(decisions),
            codec.checksum(List.of(record(participantId, 1L, 0.50)))
        );
    }

    private static CombatAiDecisionRecord record(
        UUID participantId,
        long tick,
        double confidence
    ) {
        return new CombatAiDecisionRecord(
            participantId,
            tick,
            CombatAiGoalType.ATTACK_TARGET,
            UUID.randomUUID(),
            new DecisionConfidence(confidence),
            List.of(DecisionReason.of("threatScore", "Threat score.", 50)),
            List.of("FireCombatWeaponCommand")
        );
    }
}
