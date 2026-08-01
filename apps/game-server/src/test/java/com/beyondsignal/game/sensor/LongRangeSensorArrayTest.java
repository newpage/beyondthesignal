package com.beyondsignal.game.sensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beyondsignal.game.galaxy.Galaxy;
import com.beyondsignal.game.galaxy.StarSystem;
import com.beyondsignal.game.world.UniverseGenerator;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class LongRangeSensorArrayTest {
    @Test
    void detectsOnlySystemsWithinRangeAndSortsByDistance() {
        Galaxy galaxy = new UniverseGenerator().generate(41L, 2, 8);
        StarSystem origin = galaxy.systems().findFirst().orElseThrow();
        double range = galaxy.systems()
            .filter(system -> !system.id().equals(origin.id()))
            .mapToDouble(origin::distanceTo)
            .sorted()
            .skip(2)
            .findFirst()
            .orElseThrow();

        SensorScanResult result = new LongRangeSensorArray(galaxy)
            .scan(origin.id(), range, 0.75, 99L, Instant.EPOCH);

        assertFalse(result.contacts().isEmpty());
        assertTrue(result.contacts().stream()
            .allMatch(contact -> contact.distanceLightYears() <= range));

        List<SensorContact> contacts = result.contacts();
        for (int i = 1; i < contacts.size(); i++) {
            assertTrue(contacts.get(i - 1).distanceLightYears()
                <= contacts.get(i).distanceLightYears());
        }
    }

    @Test
    void repeatedScanWithSameSeedIsDeterministic() {
        Galaxy galaxy = new UniverseGenerator().generate(88L, 1, 6);
        StarSystem origin = galaxy.systems().findFirst().orElseThrow();
        LongRangeSensorArray array = new LongRangeSensorArray(galaxy);

        SensorScanResult first = array.scan(origin.id(), 500.0, 0.5, 123L, Instant.EPOCH);
        SensorScanResult second = array.scan(origin.id(), 500.0, 0.5, 123L, Instant.EPOCH);

        assertEquals(first, second);
    }
}
