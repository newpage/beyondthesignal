package com.beyondsignal.game.combat.shield;

import com.beyondsignal.game.combat.weapon.DamageType;

public record ShieldImpact(
    ShieldQuadrant quadrant,
    DamageType damageType,
    int incomingDamage,
    int absorbedDamage,
    int penetratingDamage,
    boolean collapsed
) {
    public ShieldImpact {
        if (incomingDamage < 0 || absorbedDamage < 0 || penetratingDamage < 0) {
            throw new IllegalArgumentException("Damage values cannot be negative");
        }
        if (absorbedDamage + penetratingDamage != incomingDamage) {
            throw new IllegalArgumentException("Impact damage must balance");
        }
    }
}
