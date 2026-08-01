package com.beyondsignal.game.bridge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class PowerGridTest {
    @Test
    void enforcesTotalPowerBudget() {
        PowerGrid grid = new PowerGrid();

        // Free 5% from life support before reallocating it to engines.
        grid.allocate(Subsystem.LIFE_SUPPORT, 10);
        grid.allocate(Subsystem.ENGINES, 30);

        assertEquals(100, grid.total());
        assertEquals(30, grid.allocation(Subsystem.ENGINES));
        assertEquals(10, grid.allocation(Subsystem.LIFE_SUPPORT));

        assertThrows(
            IllegalStateException.class,
            () -> grid.allocate(Subsystem.WEAPONS, 30)
        );
    }
}
