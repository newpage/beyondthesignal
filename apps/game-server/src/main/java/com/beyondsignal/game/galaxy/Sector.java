package com.beyondsignal.game.galaxy;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record Sector(String id, String name, List<StarSystem> systems) {
    public Sector {
        id = requireText(id, "id");
        name = requireText(name, "name");
        systems = List.copyOf(Objects.requireNonNull(systems, "systems"));
    }

    public Optional<StarSystem> systemById(String systemId) {
        return systems.stream().filter(system -> system.id().equals(systemId)).findFirst();
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value;
    }
}
