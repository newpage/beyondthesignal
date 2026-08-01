package com.beyondsignal.game.simulation;

import com.beyondsignal.game.navigation.ActiveWarpRoute;
import com.beyondsignal.game.navigation.ShipPosition;
import com.beyondsignal.game.navigation.WarpDrive;
import com.beyondsignal.game.navigation.WarpRoute;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public final class WarpTravelController implements TickListener {
    private final WarpDrive warpDrive;
    private ShipPosition shipPosition;

    public WarpTravelController(WarpDrive warpDrive, ShipPosition initialPosition) {
        this.warpDrive = Objects.requireNonNull(warpDrive, "warpDrive");
        this.shipPosition = Objects.requireNonNull(initialPosition, "initialPosition");
    }

    public synchronized ActiveWarpRoute engage(WarpRoute route, Instant simulationTime) {
        Objects.requireNonNull(route, "route");
        Objects.requireNonNull(simulationTime, "simulationTime");

        if (shipPosition.inTransit()) {
            throw new IllegalStateException("Ship is already in transit");
        }
        if (!route.origin().id().equals(shipPosition.currentSystemId())) {
            throw new IllegalArgumentException(
                "Route origin does not match current ship system: " + shipPosition.currentSystemId()
            );
        }

        ActiveWarpRoute activeRoute = warpDrive.engage(route, simulationTime);
        shipPosition = activeRoute.position();
        return activeRoute;
    }

    @Override
    public synchronized void onTick(Instant simulationTime, Duration delta) {
        warpDrive.tick(delta).ifPresent(route -> shipPosition = route.position());
    }

    public synchronized Optional<ActiveWarpRoute> activeRoute() {
        return warpDrive.activeRoute();
    }

    public synchronized ShipPosition shipPosition() {
        return shipPosition;
    }

    public synchronized void clearCompletedRoute() {
        warpDrive.clearCompletedRoute();
    }
}
