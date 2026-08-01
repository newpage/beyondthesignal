package com.beyondsignal.game.combat.ai.sensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.CombatantSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.ShieldSnapshot;
import com.beyondsignal.game.combat.engine.CombatParticipantStatus;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.shield.ShieldQuadrant;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatSensorSuiteTest {
    @Test
    void findsWeakestHostileWithStableTieBreaking() {
        UUID self = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID hostileA = UUID.fromString("00000000-0000-0000-0000-000000000002");
        UUID hostileB = UUID.fromString("00000000-0000-0000-0000-000000000003");

        CombatAiSnapshot snapshot = new CombatAiSnapshot(
            CombatId.random(),
            1L,
            List.of(
                combatant(self, CombatSide.FRIENDLY, 100),
                combatant(hostileB, CombatSide.HOSTILE, 40),
                combatant(hostileA, CombatSide.HOSTILE, 40)
            )
        );

        assertEquals(
            hostileA,
            new CombatSensorSuite()
                .weakestHullHostile(snapshot, self)
                .orElseThrow()
                .participantId()
        );
    }

    private static CombatantSnapshot combatant(UUID id, CombatSide side, int hull) {
        EnumMap<ShieldQuadrant, Integer> values = new EnumMap<>(ShieldQuadrant.class);
        for (ShieldQuadrant quadrant : ShieldQuadrant.values()) {
            values.put(quadrant, 50);
        }
        return new CombatantSnapshot(
            id, side, CombatParticipantStatus.ACTIVE,
            hull, 100, new ShieldSnapshot(values, values),
            List.of(), null, 0.5, 0.2
        );
    }
}
