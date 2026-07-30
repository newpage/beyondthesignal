package com.beyondsignal.game.simulation;

import java.util.Objects;
import java.util.UUID;

public record WorldObjectState(
    UUID id,
    String displayName,
    String type,
    Vector3 position,
    Vector3 velocity,
    boolean hostile
) {
    public WorldObjectState {
        Objects.requireNonNull(id, "id must not be null");
        displayName = requireText(displayName, "displayName");
        type = requireText(type, "type");
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(velocity, "velocity must not be null");
    }

    public WorldObjectState advance(double seconds) {
        return new WorldObjectState(id, displayName, type,
            position.add(velocity.scale(seconds)), velocity, hostile);
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name + " must not be null");
        String result = value.trim();
        if (result.isEmpty()) throw new IllegalArgumentException(name + " must not be blank");
        return result;
    }
}
