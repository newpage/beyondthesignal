package com.beyondsignal.game.combat.event;

import com.beyondsignal.game.combat.model.CombatId;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record CombatEvent(
    CombatId combatId,
    long sequence,
    long tick,
    UUID sourceId,
    UUID targetId,
    CombatEventType type,
    Instant occurredAt,
    Map<String, String> payload
) {
    public CombatEvent {
        combatId = Objects.requireNonNull(combatId, "combatId");
        if (sequence < 1) throw new IllegalArgumentException("sequence must be positive");
        if (tick < 0) throw new IllegalArgumentException("tick cannot be negative");
        sourceId = Objects.requireNonNull(sourceId, "sourceId");
        type = Objects.requireNonNull(type, "type");
        occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
        payload = Map.copyOf(Objects.requireNonNull(payload, "payload"));
    }
}
