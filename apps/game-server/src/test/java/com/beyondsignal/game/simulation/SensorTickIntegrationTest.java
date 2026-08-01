package com.beyondsignal.game.simulation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beyondsignal.game.galaxy.Galaxy;
import com.beyondsignal.game.galaxy.StarSystem;
import com.beyondsignal.game.sensor.LongRangeSensorArray;
import com.beyondsignal.game.sensor.SensorController;
import com.beyondsignal.game.world.UniverseGenerator;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class SensorTickIntegrationTest {
    @Test
    void sharedTickEngineAdvancesSensorCooldown() {
        Galaxy galaxy = new UniverseGenerator().generate(63L, 1, 4);
        StarSystem origin = galaxy.systems().findFirst().orElseThrow();

        TickEngine tickEngine = new TickEngine(Instant.EPOCH);
        SensorController sensorController =
            new SensorController(new LongRangeSensorArray(galaxy));
        tickEngine.addListener(sensorController);

        sensorController.scan(origin.id(), 200.0, 1.0, 5L, tickEngine.simulationTime());
        tickEngine.advance(Duration.ofSeconds(10));

        assertTrue(sensorController.state().readyForScan());
    }
}
