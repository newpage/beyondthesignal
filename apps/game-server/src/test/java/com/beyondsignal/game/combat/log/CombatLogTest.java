package com.beyondsignal.game.combat.log;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.beyondsignal.game.combat.event.CombatEvent;
import com.beyondsignal.game.combat.event.CombatEventType;
import com.beyondsignal.game.combat.model.CombatId;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatLogTest {
    @Test
    void enforcesContiguousSequence() {
        CombatLog log = new CombatLog();
        CombatId combatId = CombatId.random();
        log.append(event(combatId, 1L));
        assertEquals(1, log.size());
        assertThrows(IllegalArgumentException.class, () -> log.append(event(combatId, 3L)));
    }

    private static CombatEvent event(CombatId id, long sequence) {
        return new CombatEvent(
            id, sequence, sequence, UUID.randomUUID(), UUID.randomUUID(),
            CombatEventType.WEAPON_FIRED, Instant.EPOCH, Map.of()
        );
    }
}
