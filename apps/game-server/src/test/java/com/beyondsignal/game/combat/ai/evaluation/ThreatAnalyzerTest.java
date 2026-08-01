package com.beyondsignal.game.combat.ai.evaluation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.CombatantSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.ShieldSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.WeaponSnapshot;
import com.beyondsignal.game.combat.engine.CombatParticipantStatus;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.shield.ShieldQuadrant;
import com.beyondsignal.game.combat.weapon.WeaponType;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ThreatAnalyzerTest {
    @Test
    void prioritizesArmedEnemyTargetingObserver() {
        UUID observerId = UUID.fromString("00000000-0000-0000-0000-000000000010");
        UUID dangerousId = UUID.fromString("00000000-0000-0000-0000-000000000011");
        UUID weakId = UUID.fromString("00000000-0000-0000-0000-000000000012");

        CombatAiSnapshot snapshot = new CombatAiSnapshot(
            CombatId.random(),
            2L,
            List.of(
                combatant(observerId, CombatSide.FRIENDLY, null, List.of()),
                combatant(dangerousId, CombatSide.HOSTILE, observerId,
                    List.of(weapon(40))),
                combatant(weakId, CombatSide.HOSTILE, null, List.of(weapon(5)))
            )
        );

        ThreatTable table = new ThreatAnalyzer().analyze(snapshot, observerId);

        assertEquals(dangerousId, table.highestThreat().orElseThrow().targetId());
        assertTrue(table.threats().get(0).total() > table.threats().get(1).total());
        assertEquals(
            25,
            table.threats().get(0).factors().get("targetingObserver")
        );
    }

    private static CombatantSnapshot combatant(
        UUID id,
        CombatSide side,
        UUID target,
        List<WeaponSnapshot> weapons
    ) {
        EnumMap<ShieldQuadrant, Integer> values = new EnumMap<>(ShieldQuadrant.class);
        for (ShieldQuadrant quadrant : ShieldQuadrant.values()) {
            values.put(quadrant, 50);
        }
        return new CombatantSnapshot(
            id, side, CombatParticipantStatus.ACTIVE,
            100, 100, new ShieldSnapshot(values, values),
            weapons, target, 0.8, 0.2
        );
    }

    private static WeaponSnapshot weapon(int damage) {
        return new WeaponSnapshot(
            UUID.randomUUID(), "weapon-" + damage, WeaponType.BEAM,
            true, 0, 0, damage, 10000
        );
    }
}
