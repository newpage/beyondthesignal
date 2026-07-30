package com.beyondsignal.game.navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beyondsignal.game.galaxy.Galaxy;
import com.beyondsignal.game.galaxy.StarSystem;
import com.beyondsignal.game.world.UniverseGenerator;
import java.util.List;
import org.junit.jupiter.api.Test;

class NavigationComputerTest {
    private final Galaxy galaxy = new UniverseGenerator().generate(99L, 2, 5);
    private final NavigationComputer navigation = new NavigationComputer(galaxy);

    @Test
    void calculatesRouteBetweenKnownSystems() {
        List<StarSystem> systems = galaxy.systems().toList();
        WarpRoute route = navigation.calculateRoute(systems.get(0).id(), systems.get(1).id(), 5);

        assertEquals(systems.get(0), route.origin());
        assertEquals(systems.get(1), route.destination());
        assertTrue(route.distanceLightYears() > 0);
        assertTrue(!route.estimatedTravelTime().isNegative());
    }

    @Test
    void nearbySystemsAreSortedByDistance() {
        StarSystem origin = galaxy.systems().findFirst().orElseThrow();
        List<StarSystem> nearby = navigation.nearbySystems(origin.id(), 4);

        assertEquals(4, nearby.size());
        for (int i = 1; i < nearby.size(); i++) {
            assertTrue(origin.distanceTo(nearby.get(i - 1)) <= origin.distanceTo(nearby.get(i)));
        }
    }

    @Test
    void rejectsUnknownSystem() {
        assertThrows(IllegalArgumentException.class,
            () -> navigation.calculateRoute("missing", "also-missing", 5));
    }
}
