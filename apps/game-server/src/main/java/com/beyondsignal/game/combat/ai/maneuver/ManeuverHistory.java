package com.beyondsignal.game.combat.ai.maneuver;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ManeuverHistory {
    private final List<ManeuverHistoryEntry> entries = new ArrayList<>();

    public synchronized void append(ManeuverHistoryEntry entry) {
        Objects.requireNonNull(entry, "entry");
        if (!entries.isEmpty() && entry.tick() < entries.getLast().tick()) {
            throw new IllegalArgumentException("Maneuver history ticks must not decrease");
        }
        entries.add(entry);
    }

    public synchronized List<ManeuverHistoryEntry> entries() {
        return List.copyOf(entries);
    }

    public synchronized ManeuverHistoryEntry latest() {
        if (entries.isEmpty()) {
            throw new IllegalStateException("Maneuver history is empty");
        }
        return entries.getLast();
    }
}
