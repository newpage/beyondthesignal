package com.beyondsignal.game.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beyondsignal.game.galaxy.Galaxy;
import com.beyondsignal.game.galaxy.StarSystem;
import com.beyondsignal.game.navigation.NavigationComputer;
import com.beyondsignal.game.navigation.ShipPosition;
import com.beyondsignal.game.navigation.WarpDrive;
import com.beyondsignal.game.navigation.WarpRoute;
import com.beyondsignal.game.navigation.WarpState;
import com.beyondsignal.game.world.UniverseGenerator;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class WarpTravelControllerTest {
    @Test
    void tickEngineMovesShipAndCompletesArrival() {
        Galaxy galaxy = new UniverseGenerator().generate(7L, 1, 3);
        List<StarSystem> systems = galaxy.systems().toList();
        StarSystem origin = systems.get(0);
        StarSystem destination = systems.get(1);

        NavigationComputer navigation = new NavigationComputer(galaxy);
        WarpRoute route = navigation.calculateRoute(origin.id(), destination.id(), 6);

        TickEngine engine = new TickEngine(Instant.parse("2400-01-01T00:00:00Z"));
        WarpTravelController controller = new WarpTravelController(
            new WarpDrive(),
            ShipPosition.atSystem(origin.id(), origin.coordinates())
        );
        engine.addListener(controller);

        controller.engage(route, engine.simulationTime());
        Duration firstHalf = route.estimatedTravelTime().dividedBy(2);
        engine.advance(firstHalf);

        assertTrue(controller.shipPosition().inTransit());
        assertTrue(controller.shipPosition().routeProgress() > 0.0);

        engine.advance(route.estimatedTravelTime());

        assertEquals(WarpState.ARRIVED, controller.activeRoute().orElseThrow().state());
        assertEquals(destination.id(), controller.shipPosition().currentSystemId());
        assertEquals(destination.coordinates(), controller.shipPosition().coordinates());
        assertFalse(controller.shipPosition().inTransit());
    }
}
