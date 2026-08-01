package com.beyondsignal.game.combat.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.shield.ShieldModel;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatParticipantTest {
    @Test
    void hullDamageCanDestroyParticipant() {
        CombatParticipant participant = participant(CombatSide.FRIENDLY);

        participant.applyHullDamage(100);

        assertEquals(0, participant.hull());
        assertEquals(CombatParticipantStatus.DESTROYED, participant.status());
        assertFalse(participant.operational());
    }

    private static CombatParticipant participant(CombatSide side) {
        return new CombatParticipant(
            UUID.randomUUID(),
            side,
            ShieldModel.uniform(50, 1),
            List.of(),
            100,
            0.8,
            0.2
        );
    }
}
