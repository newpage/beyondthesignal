package com.beyondsignal.game.service.event;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record SessionEvent(
    UUID sessionId,
    SessionEventType type,
    Instant occurredAt,
    Map<String, Object> payload
) {
    public SessionEvent {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
        payload = Map.copyOf(Objects.requireNonNull(payload, "payload must not be null"));
    }
}
