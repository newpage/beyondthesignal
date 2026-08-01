package com.beyondsignal.game.combat.damage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.rng.SplitMix64CombatRandom;
import com.beyondsignal.game.combat.shield.ShieldModel;
import com.beyondsignal.game.combat.shield.ShieldQuadrant;
import com.beyondsignal.game.combat.weapon.DamageType;
import com.beyondsignal.game.combat.weapon.WeaponArc;
import com.beyondsignal.game.combat.weapon.WeaponDefinition;
import com.beyondsignal.game.combat.weapon.WeaponType;
import org.junit.jupiter.api.Test;

class DamagePipelineTest {
    @Test
    void resolvesShieldThenHullDamageDeterministically() {
        WeaponDefinition weapon = new WeaponDefinition(
            "torpedo",
            "Photon Torpedo",
            WeaponType.PROJECTILE,
            DamageType.KINETIC,
            WeaponArc.FORWARD,
            60,
            15000,
            0.7,
            0,
            10,
            1
        );

        DamageResolution resolution = new DamagePipeline().resolve(
            weapon,
            ShieldModel.uniform(40, 0),
            ShieldQuadrant.FORWARD,
            100,
            1.0,
            1.0,
            0.0,
            new SplitMix64CombatRandom(1L)
        );

        assertEquals(40, resolution.shieldImpact().absorbedDamage());
        assertEquals(20, resolution.hullDamage());
        assertEquals(80, resolution.remainingHull());
    }

    @Test
    void marksShipDestroyedWhenHullReachesZero() {
        WeaponDefinition weapon = new WeaponDefinition(
            "heavy",
            "Heavy Beam",
            WeaponType.BEAM,
            DamageType.ENERGY,
            WeaponArc.FORWARD,
            200,
            10000,
            1.0,
            0,
            1,
            0
        );

        DamageResolution resolution = new DamagePipeline().resolve(
            weapon,
            ShieldModel.uniform(1, 0),
            ShieldQuadrant.FORWARD,
            50,
            1.0,
            1.0,
            0.0,
            new SplitMix64CombatRandom(1L)
        );

        assertTrue(resolution.destroyed());
        assertEquals(0, resolution.remainingHull());
    }
}
