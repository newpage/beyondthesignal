package com.beyondsignal.game.combat.shield;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.weapon.DamageType;
import org.junit.jupiter.api.Test;

class ShieldModelTest {
    @Test
    void absorbsDamageAndPassesExcessToHull() {
        ShieldModel shields = ShieldModel.uniform(40, 5);

        ShieldImpact impact = shields.apply(
            ShieldQuadrant.FORWARD,
            DamageType.ENERGY,
            60,
            1.0
        );

        assertEquals(40, impact.absorbedDamage());
        assertEquals(20, impact.penetratingDamage());
        assertTrue(impact.collapsed());
    }

    @Test
    void regeneratesQuadrants() {
        ShieldModel shields = ShieldModel.uniform(40, 5);
        shields.apply(ShieldQuadrant.PORT, DamageType.KINETIC, 10, 1.0);
        shields.regenerate();
        assertEquals(35, shields.state(ShieldQuadrant.PORT).strength());
    }
}
