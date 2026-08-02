package com.beyondsignal.game.combat.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beyondsignal.game.combat.model.CombatId;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatEventRecorderTest {
    @Test
    void recordsPerEncounterAndCanClearHistory() {
        CombatEventRecorder recorder = new CombatEventRecorder();
        CombatId first = CombatId.random();
        CombatId second = CombatId.random();

        recorder.onEvents(batch(first, 1));
        recorder.onEvents(batch(first, 2));
        recorder.onEvents(batch(second, 1));

        assertEquals(2, recorder.batches(first).size());
        assertEquals(2, recorder.events(first).size());
        assertEquals(1, recorder.events(second).size());

        recorder.clear(first);
        assertTrue(recorder.events(first).isEmpty());
    }

    private static CombatEventBatch batch(CombatId id, long tick) {
        return new CombatEventBatch(
            id,
            tick,
            List.of(new CombatEvent(
                id,
                tick,
                tick,
                UUID.randomUUID(),
                null,
                CombatEventType.WEAPON_FIRED,
                Instant.EPOCH.plusMillis(tick),
                Map.of("tick", Long.toString(tick))
            ))
        );
    }
}
