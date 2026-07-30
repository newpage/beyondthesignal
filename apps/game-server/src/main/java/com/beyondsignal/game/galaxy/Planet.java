package com.beyondsignal.game.galaxy;

import java.util.Objects;

public record Planet(
    String id,
    String name,
    PlanetType type,
    int orbit,
    boolean habitable,
    boolean hasResources,
    boolean hasAnomaly
) {
    public Planet {
        id = requireText(id, "id");
        name = requireText(name, "name");
        type = Objects.requireNonNull(type, "type");
        if (orbit < 1) {
            throw new IllegalArgumentException("orbit must be at least 1");
        }
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value;
    }
}
