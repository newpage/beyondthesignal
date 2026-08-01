package com.beyondsignal.game.navigation;

import com.beyondsignal.game.galaxy.Coordinates;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public record ActiveWarpRoute(
    WarpRoute route,
    Instant engagedAt,
    Duration elapsed,
    WarpState state
) {
    public ActiveWarpRoute {
        route = Objects.requireNonNull(route, "route");
        engagedAt = Objects.requireNonNull(engagedAt, "engagedAt");
        elapsed = Objects.requireNonNull(elapsed, "elapsed");
        state = Objects.requireNonNull(state, "state");
        if (elapsed.isNegative()) {
            throw new IllegalArgumentException("elapsed cannot be negative");
        }
    }

    public static ActiveWarpRoute engage(WarpRoute route, Instant engagedAt) {
        return new ActiveWarpRoute(route, engagedAt, Duration.ZERO, WarpState.AT_WARP);
    }

    public ActiveWarpRoute advance(Duration delta) {
        if (delta == null || delta.isNegative()) {
            throw new IllegalArgumentException("delta must be non-negative");
        }
        if (state != WarpState.AT_WARP) {
            return this;
        }

        Duration nextElapsed = elapsed.plus(delta);
        if (nextElapsed.compareTo(route.estimatedTravelTime()) >= 0) {
            return new ActiveWarpRoute(route, engagedAt, route.estimatedTravelTime(), WarpState.ARRIVED);
        }
        return new ActiveWarpRoute(route, engagedAt, nextElapsed, WarpState.AT_WARP);
    }

    public ActiveWarpRoute abort() {
        if (state == WarpState.ARRIVED) {
            return this;
        }
        return new ActiveWarpRoute(route, engagedAt, elapsed, WarpState.ABORTED);
    }

    public double progress() {
        long totalNanos = route.estimatedTravelTime().toNanos();
        if (totalNanos == 0L || state == WarpState.ARRIVED) {
            return 1.0;
        }
        return Math.min(1.0, elapsed.toNanos() / (double) totalNanos);
    }

    public Duration remainingTime() {
        if (state == WarpState.ARRIVED) {
            return Duration.ZERO;
        }
        Duration remaining = route.estimatedTravelTime().minus(elapsed);
        return remaining.isNegative() ? Duration.ZERO : remaining;
    }

    public ShipPosition position() {
        Coordinates origin = route.origin().coordinates();
        Coordinates destination = route.destination().coordinates();
        double progress = progress();

        Coordinates interpolated = new Coordinates(
            interpolate(origin.x(), destination.x(), progress),
            interpolate(origin.y(), destination.y(), progress),
            interpolate(origin.z(), destination.z(), progress)
        );

        if (state == WarpState.ARRIVED) {
            return ShipPosition.atSystem(route.destination().id(), destination);
        }

        return ShipPosition.inTransit(
            interpolated,
            route.origin().id(),
            route.destination().id(),
            progress
        );
    }

    private static double interpolate(double from, double to, double progress) {
        return from + (to - from) * progress;
    }
}
