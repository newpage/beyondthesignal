package com.beyondsignal.game.combat.event;

import com.beyondsignal.game.combat.model.CombatId;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public record CombatEventBatch(
    CombatId combatId,
    long tick,
    List<CombatEvent> events
) {
    public CombatEventBatch {
        combatId = Objects.requireNonNull(combatId, "combatId");
        if (tick < 1) {
            throw new IllegalArgumentException("tick must be positive");
        }
        events = List.copyOf(Objects.requireNonNull(events, "events"));

        long previousSequence = 0;
        for (CombatEvent event : events) {
            if (!combatId.equals(event.combatId())) {
                throw new IllegalArgumentException("event belongs to another combat encounter");
            }
            if (tick != event.tick()) {
                throw new IllegalArgumentException("event belongs to another combat tick");
            }
            if (event.sequence() <= previousSequence) {
                throw new IllegalArgumentException("events must be in ascending sequence order");
            }
            previousSequence = event.sequence();
        }
    }

    public static CombatEventBatch from(
        CombatId combatId,
        long tick,
        List<CombatEvent> events
    ) {
        List<CombatEvent> ordered = events.stream()
            .sorted(Comparator.comparingLong(CombatEvent::sequence))
            .toList();
        return new CombatEventBatch(combatId, tick, ordered);
    }

    public boolean empty() {
        return events.isEmpty();
    }
}
