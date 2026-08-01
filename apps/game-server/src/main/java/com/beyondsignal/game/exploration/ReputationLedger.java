package com.beyondsignal.game.exploration;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class ReputationLedger {
    private final EnumMap<Faction, Integer> values = new EnumMap<>(Faction.class);

    public ReputationLedger() {
        for (Faction faction : Faction.values()) {
            values.put(faction, 0);
        }
    }

    public synchronized int reputation(Faction faction) {
        return values.get(faction);
    }

    public synchronized int adjust(Faction faction, int delta) {
        int next = Math.max(-100, Math.min(100, values.get(faction) + delta));
        values.put(faction, next);
        return next;
    }

    public synchronized Map<Faction, Integer> snapshot() {
        return Collections.unmodifiableMap(new EnumMap<>(values));
    }
}
