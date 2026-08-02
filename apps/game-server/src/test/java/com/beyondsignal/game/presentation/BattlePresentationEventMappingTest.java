package com.beyondsignal.game.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.event.CombatEvent;
import com.beyondsignal.game.combat.event.CombatEventType;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.presentation.context.PresentationConfiguration;
import com.beyondsignal.game.presentation.context.PresentationContext;
import com.beyondsignal.game.presentation.context.PresentationDebugOptions;
import com.beyondsignal.game.presentation.mapper.BattlePresentationMapper;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BattlePresentationEventMappingTest {
    @Test
    void exposesCombatEventsInBattleFrame() {
        CombatId id = CombatId.random();
        UUID source = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        CombatEvent event = new CombatEvent(
            id,
            1,
            4,
            source,
            target,
            CombatEventType.WEAPON_FIRED,
            Instant.EPOCH,
            Map.of("weaponId", "phaser-mk1")
        );

        var frame = new BattlePresentationMapper().map(
            new PresentationContext(
                new CombatAiSnapshot(id, 4, List.of()),
                12,
                2,
                Instant.EPOCH,
                PresentationConfiguration.defaults(),
                PresentationDebugOptions.disabled(),
                List.of(event)
            )
        );

        assertEquals(1, frame.events().size());
        assertEquals("WEAPON_FIRED", frame.events().getFirst().type());
        assertEquals(source.toString(),
            frame.events().getFirst().attributes().get("sourceId"));
        assertEquals("phaser-mk1",
            frame.events().getFirst().attributes().get("weaponId"));
    }
}
