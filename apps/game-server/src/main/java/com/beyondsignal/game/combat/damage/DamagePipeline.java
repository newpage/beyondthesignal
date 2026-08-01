package com.beyondsignal.game.combat.damage;

import com.beyondsignal.game.combat.rng.CombatRandom;
import com.beyondsignal.game.combat.shield.ShieldImpact;
import com.beyondsignal.game.combat.shield.ShieldModel;
import com.beyondsignal.game.combat.shield.ShieldQuadrant;
import com.beyondsignal.game.combat.weapon.WeaponDefinition;
import java.util.Objects;

public final class DamagePipeline {
    public DamageResolution resolve(
        WeaponDefinition weapon,
        ShieldModel shields,
        ShieldQuadrant impactedQuadrant,
        int currentHull,
        double shieldResistanceModifier,
        double hullDamageModifier,
        double criticalChance,
        CombatRandom random
    ) {
        Objects.requireNonNull(weapon, "weapon");
        Objects.requireNonNull(shields, "shields");
        Objects.requireNonNull(impactedQuadrant, "impactedQuadrant");
        Objects.requireNonNull(random, "random");

        if (currentHull < 0) {
            throw new IllegalArgumentException("currentHull cannot be negative");
        }
        if (!Double.isFinite(hullDamageModifier) || hullDamageModifier < 0.0) {
            throw new IllegalArgumentException("hullDamageModifier must be non-negative");
        }
        if (!Double.isFinite(criticalChance) || criticalChance < 0.0 || criticalChance > 1.0) {
            throw new IllegalArgumentException("criticalChance must be between 0.0 and 1.0");
        }

        ShieldImpact impact = shields.apply(
            impactedQuadrant,
            weapon.damageType(),
            weapon.baseDamage(),
            shieldResistanceModifier
        );

        int hullDamage = Math.min(
            currentHull,
            (int) Math.ceil(impact.penetratingDamage() * hullDamageModifier)
        );

        boolean critical = hullDamage > 0 && random.chance(criticalChance);
        if (critical) {
            hullDamage = Math.min(currentHull, hullDamage + Math.max(1, hullDamage / 2));
        }

        int remainingHull = Math.max(0, currentHull - hullDamage);
        return new DamageResolution(
            impact,
            hullDamage,
            critical,
            remainingHull,
            remainingHull == 0
        );
    }
}
