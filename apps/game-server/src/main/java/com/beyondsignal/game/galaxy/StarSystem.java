package com.beyondsignal.game.galaxy;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record StarSystem(
    String id,
    String name,
    Coordinates coordinates,
    String starClass,
    List<Planet> planets,
    boolean hasStation,
    boolean hostileTerritory,
    boolean visited,
    boolean mapped
) {
    public StarSystem {
        id = requireText(id, "id");
        name = requireText(name, "name");
        coordinates = Objects.requireNonNull(coordinates, "coordinates");
        starClass = requireText(starClass, "starClass");
        planets = List.copyOf(Objects.requireNonNull(planets, "planets"));
    }

    public double distanceTo(StarSystem other) {
        Objects.requireNonNull(other, "other");
        return coordinates.distanceTo(other.coordinates);
    }

    public Optional<Planet> planetById(String planetId) {
        return planets.stream().filter(planet -> planet.id().equals(planetId)).findFirst();
    }

    public StarSystem markVisited() {
        return new StarSystem(id, name, coordinates, starClass, planets, hasStation,
            hostileTerritory, true, mapped);
    }

    public StarSystem markMapped() {
        return new StarSystem(id, name, coordinates, starClass, planets, hasStation,
            hostileTerritory, visited, true);
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value;
    }
}
