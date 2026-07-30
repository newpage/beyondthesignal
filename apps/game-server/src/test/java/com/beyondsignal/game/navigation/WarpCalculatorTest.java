package com.beyondsignal.game.navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class WarpCalculatorTest {
    private final WarpCalculator calculator = new WarpCalculator();

    @Test
    void higherWarpFactorReducesTravelTime() {
        Duration warpThree = calculator.estimateTravelTime(4.2, 3);
        Duration warpSix = calculator.estimateTravelTime(4.2, 6);
        assertTrue(warpSix.compareTo(warpThree) < 0);
    }

    @Test
    void distanceAndEtaAreInternallyConsistent() {
        Duration eta = calculator.estimateTravelTime(8.7, 5);
        assertEquals(8.7, calculator.distanceTravelled(eta, 5), 0.001);
    }

    @Test
    void rejectsUnsupportedWarpFactor() {
        assertThrows(IllegalArgumentException.class,
            () -> calculator.estimateTravelTime(1.0, 10));
    }
}
