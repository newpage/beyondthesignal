package com.beyondsignal.game.navigation;

import com.beyondsignal.game.galaxy.StarSystem;
import java.time.Duration;
import java.util.Objects;

public record WarpRoute(
    StarSystem origin,
    StarSystem destination,
    double distanceLightYears,
    int warpFactor,
    Duration estimatedTravelTime
) {
    public WarpRoute {
        origin = Objects.requireNonNull(origin, "origin");
        destination = Objects.requireNonNull(destination, "destination");
        estimatedTravelTime = Objects.requireNonNull(estimatedTravelTime, "estimatedTravelTime");
        if (distanceLightYears < 0.0 || !Double.isFinite(distanceLightYears)) {
            throw new IllegalArgumentException("distanceLightYears must be finite and non-negative");
        }
        if (warpFactor < 1 || warpFactor > 9) {
            throw new IllegalArgumentException("warpFactor must be between 1 and 9");
        }
    }
}
