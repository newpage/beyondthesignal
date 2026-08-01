package com.beyondsignal.game.combat.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.beyondsignal.game.combat.model.CombatId;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatEventTest {
    @Test
    void copiesPayloadDefensively() {
        CombatEvent event = new CombatEvent(
            CombatId.random(), 1L, 10L, UUID.randomUUID(), UUID.randomUUID(),
            CombatEventType.WEAPON_FIRED, Instant.EPOCH, Map.of("weaponId", "phaser-1")
        );
        assertEquals("phaser-1", event.payload().get("weaponId"));
        assertThrows(
            UnsupportedOperationException.class,
            () -> event.payload().put("damage", "25")
        );
    }
}
