package com.beyondsignal.game.combat.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beyondsignal.game.combat.model.CombatId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatEventBusTest {
    @Test
    void publishesInSubscriptionOrderAndSupportsUnsubscribe() {
        CombatEventBus bus = new CombatEventBus();
        List<String> calls = new ArrayList<>();
        CombatEventSubscription first = bus.subscribe(batch -> calls.add("first"));
        bus.subscribe(batch -> calls.add("second"));

        bus.publish(batch());
        assertEquals(List.of("first", "second"), calls);
        assertTrue(first.active());

        first.close();
        assertFalse(first.active());
        calls.clear();
        bus.publish(batch());
        assertEquals(List.of("second"), calls);
    }

    private static CombatEventBatch batch() {
        CombatId id = CombatId.random();
        UUID source = UUID.randomUUID();
        return new CombatEventBatch(
            id,
            1,
            List.of(new CombatEvent(
                id,
                1,
                1,
                source,
                null,
                CombatEventType.WEAPON_FIRED,
                Instant.EPOCH,
                Map.of()
            ))
        );
    }
}
