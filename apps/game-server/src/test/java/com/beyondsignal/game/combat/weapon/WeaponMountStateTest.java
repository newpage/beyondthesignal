package com.beyondsignal.game.combat.weapon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class WeaponMountStateTest {
    @Test
    void consumesAmmunitionAndAppliesCooldown() {
        WeaponMountState mount = WeaponMountState.ready(
            UUID.randomUUID(),
            weapon(2, 3, 1),
            4
        );

        mount = mount.beginCharge();
        assertFalse(mount.readyToFire());

        mount = mount.tick().tick();
        assertTrue(mount.readyToFire());

        mount = mount.fire();
        assertEquals(3, mount.ammunitionRemaining());
        assertEquals(3, mount.cooldownRemainingTicks());
        assertFalse(mount.readyToFire());
    }

    private static WeaponDefinition weapon(int charge, int cooldown, int ammoPerShot) {
        return new WeaponDefinition(
            "phaser",
            "Phaser",
            WeaponType.BEAM,
            DamageType.ENERGY,
            WeaponArc.FORWARD,
            25,
            10000,
            0.8,
            charge,
            cooldown,
            ammoPerShot
        );
    }
}
