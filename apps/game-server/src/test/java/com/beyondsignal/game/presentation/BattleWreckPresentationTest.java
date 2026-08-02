package com.beyondsignal.game.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.wreck.WreckState;
import com.beyondsignal.game.presentation.context.PresentationConfiguration;
import com.beyondsignal.game.presentation.context.PresentationContext;
import com.beyondsignal.game.presentation.context.PresentationDebugOptions;
import com.beyondsignal.game.presentation.mapper.BattlePresentationMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BattleWreckPresentationTest {
    @Test
    void mapsPersistentWrecks() {
        WreckState wreck = new WreckState(
            UUID.randomUUID(),
            UUID.randomUUID(),
            CombatSide.HOSTILE,
            12,
            "torpedo"
        );

        var frame = new BattlePresentationMapper().map(
            new PresentationContext(
                new CombatAiSnapshot(CombatId.random(), 20, List.of()),
                1,
                1,
                Instant.EPOCH,
                PresentationConfiguration.defaults(),
                PresentationDebugOptions.disabled(),
                List.of(),
                List.of(),
                List.of(wreck)
            )
        );

        assertEquals(1, frame.wrecks().size());
        assertEquals(wreck.wreckId(), frame.wrecks().getFirst().id());
    }
}
