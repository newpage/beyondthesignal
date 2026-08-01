package com.beyondsignal.game.navigation;

import com.beyondsignal.game.galaxy.Coordinates;
import java.util.Objects;
import java.util.Optional;

public record ShipPosition(
    Coordinates coordinates,
    String currentSystemId,
    String destinationSystemId,
    double routeProgress
) {
    public ShipPosition {
        coordinates = Objects.requireNonNull(coordinates, "coordinates");
        if (!Double.isFinite(routeProgress) || routeProgress < 0.0 || routeProgress > 1.0) {
            throw new IllegalArgumentException("routeProgress must be between 0.0 and 1.0");
        }
        if (currentSystemId != null && currentSystemId.isBlank()) {
            throw new IllegalArgumentException("currentSystemId cannot be blank");
        }
        if (destinationSystemId != null && destinationSystemId.isBlank()) {
            throw new IllegalArgumentException("destinationSystemId cannot be blank");
        }
    }

    public static ShipPosition atSystem(String systemId, Coordinates coordinates) {
        if (systemId == null || systemId.isBlank()) {
            throw new IllegalArgumentException("systemId is required");
        }
        return new ShipPosition(coordinates, systemId, null, 0.0);
    }

    public static ShipPosition inTransit(
        Coordinates coordinates,
        String originSystemId,
        String destinationSystemId,
        double progress
    ) {
        return new ShipPosition(coordinates, originSystemId, destinationSystemId, progress);
    }

    public boolean inTransit() {
        return destinationSystemId != null && routeProgress < 1.0;
    }

    public Optional<String> destination() {
        return Optional.ofNullable(destinationSystemId);
    }
}
