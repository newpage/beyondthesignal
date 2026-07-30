package com.beyondsignal.game.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Player(
    UUID id,
    String displayName,
    boolean connected,
    Instant joinedAt,
    boolean host
) {
    public Player {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(joinedAt, "joinedAt must not be null");
        displayName = requireText(displayName, "displayName");
    }

    public static Player host(String displayName, Instant joinedAt) {
        return new Player(UUID.randomUUID(), displayName, true, joinedAt, true);
    }

    public static Player crewMember(String displayName, Instant joinedAt) {
        return new Player(UUID.randomUUID(), displayName, true, joinedAt, false);
    }

    public Player disconnect() {
        return connected ? new Player(id, displayName, false, joinedAt, host) : this;
    }

    public Player reconnect() {
        return connected ? this : new Player(id, displayName, true, joinedAt, host);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
