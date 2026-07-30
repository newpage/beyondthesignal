package com.beyondsignal.game.galaxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class CoordinatesTest {
    @Test
    void calculatesThreeDimensionalDistance() {
        Coordinates a = new Coordinates(0, 0, 0);
        Coordinates b = new Coordinates(3, 4, 12);
        assertEquals(13.0, a.distanceTo(b), 0.000001);
    }
}
