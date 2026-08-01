package com.beyondsignal.game.bridge;

import java.time.Instant;
import java.util.Objects;

public record BridgeMessage(
    long sequence,
    BridgeStation station,
    String severity,
    String text,
    Instant createdAt
) {
    public BridgeMessage {
        if (sequence < 1) {
            throw new IllegalArgumentException("sequence must be positive");
        }
        station = Objects.requireNonNull(station, "station");
        severity = Objects.requireNonNull(severity, "severity");
        text = Objects.requireNonNull(text, "text");
        createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }
}
