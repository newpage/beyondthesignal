package com.beyondsignal.game.galaxy;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public record Galaxy(long seed, List<Sector> sectors) {
    public Galaxy {
        sectors = List.copyOf(Objects.requireNonNull(sectors, "sectors"));
        if (sectors.isEmpty()) {
            throw new IllegalArgumentException("Galaxy must contain at least one sector");
        }
    }

    public Stream<StarSystem> systems() {
        return sectors.stream().flatMap(sector -> sector.systems().stream());
    }

    public Optional<StarSystem> systemById(String systemId) {
        return systems().filter(system -> system.id().equals(systemId)).findFirst();
    }

    public int systemCount() {
        return Math.toIntExact(systems().count());
    }

    public int visitedSystemCount() {
        return Math.toIntExact(systems().filter(StarSystem::visited).count());
    }

    public int mappedSystemCount() {
        return Math.toIntExact(systems().filter(StarSystem::mapped).count());
    }
}
