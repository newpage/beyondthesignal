package com.beyondsignal.game.combat.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.beyondsignal.game.combat.model.CombatId;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatEventBatchTest {
    @Test
    void normalizesEventsToDeterministicSequenceOrder() {
        CombatId id = CombatId.random();
        CombatEvent first = event(id, 1);
        CombatEvent second = event(id, 2);

        CombatEventBatch batch = CombatEventBatch.from(
            id,
            5,
            List.of(second, first)
        );

        assertEquals(List.of(first, second), batch.events());
    }

    @Test
    void rejectsEventsFromAnotherTick() {
        CombatId id = CombatId.random();
        assertThrows(
            IllegalArgumentException.class,
            () -> new CombatEventBatch(id, 4, List.of(event(id, 1)))
        );
    }

    private static CombatEvent event(CombatId id, long sequence) {
        return new CombatEvent(
            id,
            sequence,
            5,
            UUID.randomUUID(),
            null,
            CombatEventType.WEAPON_FIRED,
            Instant.EPOCH,
            Map.of()
        );
    }
}
