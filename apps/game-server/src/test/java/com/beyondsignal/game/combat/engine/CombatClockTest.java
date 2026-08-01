package com.beyondsignal.game.combat.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class CombatClockTest {
    @Test
    void advancesDeterministically() {
        CombatClock clock = new CombatClock();

        assertEquals(0L, clock.currentTick());
        assertEquals(1L, clock.advance());
        assertEquals(2L, clock.advance());
    }

    @Test
    void rejectsNegativeStartingTick() {
        assertThrows(IllegalArgumentException.class, () -> new CombatClock(-1L));
    }
}
