package com.beyondsignal.game.combat.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.shield.ShieldModel;
import com.beyondsignal.game.combat.weapon.DamageType;
import com.beyondsignal.game.combat.weapon.WeaponArc;
import com.beyondsignal.game.combat.weapon.WeaponDefinition;
import com.beyondsignal.game.combat.weapon.WeaponType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BeamDamageResolverTest {
    @Test
    void absorbsDamageInShieldBeforeHull() {
        CombatParticipant target = target(20, 100);
        BeamDamageResolution result = new BeamDamageResolver().resolve(
            target,
            beam(15)
        );

        assertEquals(15, result.shieldImpact().absorbedDamage());
        assertEquals(0, result.hullDamage());
        assertEquals(100, target.hull());
        assertFalse(result.shieldCollapsedNow());
    }

    @Test
    void penetratesCollapsedShieldAndDestroysTarget() {
        CombatParticipant target = target(5, 8);
        BeamDamageResolution result = new BeamDamageResolver().resolve(
            target,
            beam(20)
        );

        assertEquals(5, result.shieldImpact().absorbedDamage());
        assertEquals(15, result.shieldImpact().penetratingDamage());
        assertEquals(8, result.hullDamage());
        assertTrue(result.shieldCollapsedNow());
        assertTrue(result.targetDestroyed());
    }

    private static CombatParticipant target(int shield, int hull) {
        return new CombatParticipant(
            UUID.randomUUID(),
            CombatSide.HOSTILE,
            ShieldModel.uniform(shield, 0),
            List.of(),
            hull,
            0.5,
            0.0
        );
    }

    private static WeaponDefinition beam(int damage) {
        return new WeaponDefinition(
            "beam",
            "Beam",
            WeaponType.BEAM,
            DamageType.ENERGY,
            WeaponArc.FORWARD,
            damage,
            10_000,
            1.0,
            0,
            1,
            0
        );
    }
}
