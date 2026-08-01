package com.beyondsignal.game.combat.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.beyondsignal.game.combat.rng.SplitMix64CombatRandom;
import com.beyondsignal.game.combat.weapon.DamageType;
import com.beyondsignal.game.combat.weapon.WeaponArc;
import com.beyondsignal.game.combat.weapon.WeaponDefinition;
import com.beyondsignal.game.combat.weapon.WeaponType;
import org.junit.jupiter.api.Test;

class HitResolverTest {
    @Test
    void sameSeedProducesSameHitResolution() {
        WeaponDefinition weapon = new WeaponDefinition(
            "phaser",
            "Phaser",
            WeaponType.BEAM,
            DamageType.ENERGY,
            WeaponArc.FORWARD,
            25,
            10000,
            0.8,
            0,
            5,
            0
        );

        HitResolution first = new HitResolver().resolve(
            weapon, 5000, 0.8, 0.2, 1.0, new SplitMix64CombatRandom(99L)
        );
        HitResolution second = new HitResolver().resolve(
            weapon, 5000, 0.8, 0.2, 1.0, new SplitMix64CombatRandom(99L)
        );

        assertEquals(first, second);
    }
}
