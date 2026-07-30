package com.beyondsignal.game.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record StationAssignment(
    BridgeStation station,
    UUID playerId,
    Instant assignedAt
) {
    public StationAssignment {
        Objects.requireNonNull(station, "station must not be null");
        Objects.requireNonNull(playerId, "playerId must not be null");
        Objects.requireNonNull(assignedAt, "assignedAt must not be null");
    }
}
