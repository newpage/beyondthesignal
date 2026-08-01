package com.beyondsignal.game.exploration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beyondsignal.game.sensor.SensorContact;
import com.beyondsignal.game.sensor.SensorContactType;
import com.beyondsignal.game.sensor.SensorScanResult;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExplorationMapTest {
    @Test
    void upgradesUnknownContactWhenLaterIdentified() {
        ExplorationMap map = new ExplorationMap();

        SensorContact unknown = new SensorContact(
            "system-1", "Unknown Contact", SensorContactType.UNKNOWN,
            4.2, 0.30, false, false
        );
        SensorContact identified = new SensorContact(
            "system-1", "Vega-1A", SensorContactType.STAR_SYSTEM,
            4.2, 0.72, false, true
        );

        map.apply(new SensorScanResult("origin", 10.0, Instant.EPOCH, List.of(unknown)));
        map.apply(new SensorScanResult(
            "origin", 10.0, Instant.EPOCH.plusSeconds(1), List.of(identified)
        ));

        assertEquals(1, map.contacts().size());
        assertEquals(1, map.identifiedCount());
        assertTrue(map.contact("system-1").orElseThrow().identified());
        assertEquals("Vega-1A", map.contact("system-1").orElseThrow().displayName());
    }
}
