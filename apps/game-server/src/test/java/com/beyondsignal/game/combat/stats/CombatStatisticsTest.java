package com.beyondsignal.game.combat.stats;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.beyondsignal.game.combat.event.CombatEvent;
import com.beyondsignal.game.combat.event.CombatEventType;
import com.beyondsignal.game.combat.model.CombatId;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatStatisticsTest {
    @Test
    void derivesStatisticsFromEvents() {
        CombatId id = CombatId.random();
        UUID source = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        List<CombatEvent> events = List.of(
            event(id,1,source,target,CombatEventType.WEAPON_FIRED),
            event(id,2,source,target,CombatEventType.WEAPON_HIT),
            event(id,3,source,target,CombatEventType.SHIELD_IMPACT),
            event(id,4,source,target,CombatEventType.WEAPON_FIRED),
            event(id,5,source,target,CombatEventType.WEAPON_MISSED)
        );
        CombatStatistics statistics = CombatStatistics.from(events);
        assertEquals(2L, statistics.weaponsFired());
        assertEquals(1L, statistics.hits());
        assertEquals(1L, statistics.misses());
        assertEquals(0.5, statistics.hitRate(), 0.000001);
    }

    private static CombatEvent event(
        CombatId id, long sequence, UUID source, UUID target, CombatEventType type
    ) {
        return new CombatEvent(
            id, sequence, sequence, source, target, type, Instant.EPOCH, Map.of()
        );
    }
}
