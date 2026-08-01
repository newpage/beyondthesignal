package com.beyondsignal.game.combat.stats;

import com.beyondsignal.game.combat.event.CombatEvent;
import com.beyondsignal.game.combat.event.CombatEventType;
import java.util.List;
import java.util.Objects;

public record CombatStatistics(
    long weaponsFired,
    long hits,
    long misses,
    long shieldImpacts,
    long hullDamageEvents,
    long criticalHits,
    long shipsDestroyed
) {
    public static CombatStatistics from(List<CombatEvent> events) {
        Objects.requireNonNull(events, "events");
        return new CombatStatistics(
            count(events, CombatEventType.WEAPON_FIRED),
            count(events, CombatEventType.WEAPON_HIT),
            count(events, CombatEventType.WEAPON_MISSED),
            count(events, CombatEventType.SHIELD_IMPACT),
            count(events, CombatEventType.HULL_DAMAGE),
            count(events, CombatEventType.CRITICAL_HIT),
            count(events, CombatEventType.SHIP_DESTROYED)
        );
    }

    public double hitRate() {
        return weaponsFired == 0 ? 0.0 : hits / (double) weaponsFired;
    }

    private static long count(List<CombatEvent> events, CombatEventType type) {
        return events.stream().filter(event -> event.type() == type).count();
    }
}
