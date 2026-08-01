package com.beyondsignal.game.navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beyondsignal.game.galaxy.Coordinates;
import com.beyondsignal.game.galaxy.StarSystem;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class WarpDriveTest {
    @Test
    void preventsEngagingWhileAlreadyAtWarp() {
        WarpDrive drive = new WarpDrive();
        WarpRoute route = route();

        drive.engage(route, Instant.EPOCH);

        assertThrows(IllegalStateException.class, () -> drive.engage(route, Instant.EPOCH));
    }

    @Test
    void advancesAndClearsCompletedRoute() {
        WarpDrive drive = new WarpDrive();
        drive.engage(route(), Instant.EPOCH);

        drive.tick(Duration.ofHours(2));

        assertEquals(WarpState.ARRIVED, drive.state());
        assertTrue(drive.position().isPresent());
        drive.clearCompletedRoute();
        assertEquals(WarpState.IDLE, drive.state());
    }

    private static WarpRoute route() {
        StarSystem origin = new StarSystem("a", "A", new Coordinates(0, 0, 0), "G",
            List.of(), false, false, false, false);
        StarSystem destination = new StarSystem("b", "B", new Coordinates(1, 0, 0), "K",
            List.of(), false, false, false, false);
        return new WarpRoute(origin, destination, 1.0, 5, Duration.ofHours(1));
    }
}
