package com.beyondsignal.game.navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beyondsignal.game.galaxy.Coordinates;
import com.beyondsignal.game.galaxy.StarSystem;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class ActiveWarpRouteTest {
    private final StarSystem origin = system("origin", 0, 0, 0);
    private final StarSystem destination = system("destination", 10, 20, 30);
    private final WarpRoute route = new WarpRoute(
        origin, destination, origin.distanceTo(destination), 6, Duration.ofHours(10)
    );

    @Test
    void interpolatesPositionAtHalfwayPoint() {
        ActiveWarpRoute active = ActiveWarpRoute.engage(route, Instant.EPOCH)
            .advance(Duration.ofHours(5));

        ShipPosition position = active.position();

        assertEquals(0.5, active.progress(), 0.000001);
        assertEquals(new Coordinates(5, 10, 15), position.coordinates());
        assertTrue(position.inTransit());
    }

    @Test
    void clampsTravelAtArrival() {
        ActiveWarpRoute active = ActiveWarpRoute.engage(route, Instant.EPOCH)
            .advance(Duration.ofHours(20));

        assertEquals(WarpState.ARRIVED, active.state());
        assertEquals(Duration.ZERO, active.remainingTime());
        assertEquals(destination.coordinates(), active.position().coordinates());
        assertEquals(destination.id(), active.position().currentSystemId());
        assertFalse(active.position().inTransit());
    }

    private static StarSystem system(String id, double x, double y, double z) {
        return new StarSystem(id, id, new Coordinates(x, y, z), "G", List.of(),
            false, false, false, false);
    }
}
