package com.beyondsignal.game.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.CombatantSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.ShieldSnapshot;
import com.beyondsignal.game.combat.engine.CombatParticipantStatus;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.shield.ShieldQuadrant;
import com.beyondsignal.game.presentation.context.PresentationConfiguration;
import com.beyondsignal.game.presentation.context.PresentationContext;
import com.beyondsignal.game.presentation.context.PresentationDebugOptions;
import com.beyondsignal.game.presentation.mapper.BattlePresentationMapper;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BattlePresentationMapperTest {
    @Test
    void mapsSnapshotDeterministicallyInParticipantOrder() {
        UUID later = UUID.fromString(
            "00000000-0000-0000-0000-000000000002"
        );
        UUID earlier = UUID.fromString(
            "00000000-0000-0000-0000-000000000001"
        );

        CombatAiSnapshot snapshot = new CombatAiSnapshot(
            new CombatId(UUID.fromString(
                "00000000-0000-0000-0000-000000000099"
            )),
            12,
            List.of(combatant(later), combatant(earlier))
        );

        var context = new PresentationContext(
            snapshot,
            44,
            8,
            Instant.EPOCH,
            PresentationConfiguration.defaults(),
            PresentationDebugOptions.development()
        );

        var first = new BattlePresentationMapper().map(context);
        var second = new BattlePresentationMapper().map(context);

        assertEquals(first, second);
        assertEquals(earlier, first.ships().getFirst().id());
        assertEquals("8", first.debug().get("frameSequence"));
        assertTrue(first.metadata().checksum().isBlank());
    }

    private static CombatantSnapshot combatant(UUID id) {
        EnumMap<ShieldQuadrant, Integer> strength =
            new EnumMap<>(ShieldQuadrant.class);
        EnumMap<ShieldQuadrant, Integer> maximum =
            new EnumMap<>(ShieldQuadrant.class);
        for (ShieldQuadrant quadrant : ShieldQuadrant.values()) {
            strength.put(quadrant, 50);
            maximum.put(quadrant, 100);
        }

        return new CombatantSnapshot(
            id,
            CombatSide.FRIENDLY,
            CombatParticipantStatus.ACTIVE,
            75,
            100,
            new ShieldSnapshot(strength, maximum),
            List.of(),
            null,
            0.8,
            0.2
        );
    }
}
