package com.beyondsignal.game.navigation;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public final class WarpDrive {
    private ActiveWarpRoute activeRoute;

    public synchronized ActiveWarpRoute engage(WarpRoute route, Instant engagedAt) {
        Objects.requireNonNull(route, "route");
        Objects.requireNonNull(engagedAt, "engagedAt");

        if (activeRoute != null && activeRoute.state() == WarpState.AT_WARP) {
            throw new IllegalStateException("Warp drive is already engaged");
        }

        activeRoute = ActiveWarpRoute.engage(route, engagedAt);
        return activeRoute;
    }

    public synchronized Optional<ActiveWarpRoute> tick(Duration delta) {
        if (activeRoute == null) {
            return Optional.empty();
        }
        activeRoute = activeRoute.advance(delta);
        return Optional.of(activeRoute);
    }

    public synchronized Optional<ActiveWarpRoute> abort() {
        if (activeRoute == null) {
            return Optional.empty();
        }
        activeRoute = activeRoute.abort();
        return Optional.of(activeRoute);
    }

    public synchronized Optional<ActiveWarpRoute> activeRoute() {
        return Optional.ofNullable(activeRoute);
    }

    public synchronized WarpState state() {
        return activeRoute == null ? WarpState.IDLE : activeRoute.state();
    }

    public synchronized Optional<ShipPosition> position() {
        return activeRoute == null ? Optional.empty() : Optional.of(activeRoute.position());
    }

    public synchronized void clearCompletedRoute() {
        if (activeRoute != null
            && (activeRoute.state() == WarpState.ARRIVED || activeRoute.state() == WarpState.ABORTED)) {
            activeRoute = null;
        }
    }
}
