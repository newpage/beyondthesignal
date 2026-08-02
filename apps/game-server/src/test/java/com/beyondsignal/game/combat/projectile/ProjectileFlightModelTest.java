package com.beyondsignal.game.combat.projectile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class ProjectileFlightModelTest {
    @Test
    void clampsTravelTimeDeterministically() {
        ProjectileFlightModel model = new ProjectileFlightModel(
            100.0,
            2,
            10
        );
        assertEquals(2, model.travelTicks(20));
        assertEquals(5, model.travelTicks(450));
        assertEquals(10, model.travelTicks(5_000));
    }
}
