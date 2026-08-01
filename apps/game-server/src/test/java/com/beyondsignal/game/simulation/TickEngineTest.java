package com.beyondsignal.game.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class TickEngineTest {
    @Test
    void advancesTimeAndNotifiesListeners() {
        TickEngine engine = new TickEngine(Instant.parse("2400-01-01T00:00:00Z"));
        AtomicInteger calls = new AtomicInteger();
        engine.addListener((time, delta) -> calls.incrementAndGet());

        Instant result = engine.advance(Duration.ofMinutes(15));

        assertEquals(Instant.parse("2400-01-01T00:15:00Z"), result);
        assertEquals(1, calls.get());
    }

    @Test
    void rejectsNonPositiveTicks() {
        TickEngine engine = new TickEngine(Instant.EPOCH);
        assertThrows(IllegalArgumentException.class, () -> engine.advance(Duration.ZERO));
    }
}
