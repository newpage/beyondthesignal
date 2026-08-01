package com.beyondsignal.game.combat.weapon;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class WeaponDefinitionTest {
    @Test
    void validatesAccuracyAndDamage() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new WeaponDefinition(
                "bad",
                "Bad Weapon",
                WeaponType.BEAM,
                DamageType.ENERGY,
                WeaponArc.FORWARD,
                0,
                1000,
                0.8,
                0,
                1,
                0
            )
        );
    }
}
