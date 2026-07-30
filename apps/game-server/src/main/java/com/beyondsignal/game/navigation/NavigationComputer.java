package com.beyondsignal.game.navigation;

import com.beyondsignal.game.galaxy.Galaxy;
import com.beyondsignal.game.galaxy.StarSystem;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class NavigationComputer {
    private final Galaxy galaxy;
    private final WarpCalculator warpCalculator;

    public NavigationComputer(Galaxy galaxy) {
        this(galaxy, new WarpCalculator());
    }

    public NavigationComputer(Galaxy galaxy, WarpCalculator warpCalculator) {
        this.galaxy = Objects.requireNonNull(galaxy, "galaxy");
        this.warpCalculator = Objects.requireNonNull(warpCalculator, "warpCalculator");
    }

    public WarpRoute calculateRoute(String originSystemId, String destinationSystemId, int warpFactor) {
        StarSystem origin = requireSystem(originSystemId);
        StarSystem destination = requireSystem(destinationSystemId);
        double distance = origin.distanceTo(destination);

        return new WarpRoute(
            origin,
            destination,
            distance,
            warpFactor,
            warpCalculator.estimateTravelTime(distance, warpFactor)
        );
    }

    public List<StarSystem> nearbySystems(String originSystemId, int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("limit must be at least 1");
        }

        StarSystem origin = requireSystem(originSystemId);
        return galaxy.systems()
            .filter(system -> !system.id().equals(origin.id()))
            .sorted(Comparator.comparingDouble(origin::distanceTo))
            .limit(limit)
            .toList();
    }

    private StarSystem requireSystem(String systemId) {
        return galaxy.systemById(systemId)
            .orElseThrow(() -> new IllegalArgumentException("Unknown star system: " + systemId));
    }
}
