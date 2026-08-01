package com.beyondsignal.game.sensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beyondsignal.game.galaxy.Galaxy;
import com.beyondsignal.game.galaxy.StarSystem;
import com.beyondsignal.game.world.UniverseGenerator;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class SensorControllerTest {
    @Test
    void enforcesCooldownAndBecomesReadyAfterTicks() {
        Galaxy galaxy = new UniverseGenerator().generate(51L, 1, 5);
        StarSystem origin = galaxy.systems().findFirst().orElseThrow();
        SensorController controller = new SensorController(new LongRangeSensorArray(galaxy));

        controller.scan(origin.id(), 500.0, 0.8, 1L, Instant.EPOCH);

        assertThrows(IllegalStateException.class,
            () -> controller.scan(origin.id(), 500.0, 0.8, 2L, Instant.EPOCH));

        controller.onTick(Instant.EPOCH.plusSeconds(10), Duration.ofSeconds(10));

        assertTrue(controller.state().readyForScan());
        controller.scan(origin.id(), 500.0, 0.8, 2L, Instant.EPOCH.plusSeconds(10));
        assertEquals(2L, controller.state().scansCompleted());
    }
}
