package com.beyondsignal.game.combat.damage;

import com.beyondsignal.game.combat.shield.ShieldImpact;

public record DamageResolution(
    ShieldImpact shieldImpact,
    int hullDamage,
    boolean criticalHit,
    int remainingHull,
    boolean destroyed
) {
    public DamageResolution {
        if (hullDamage < 0 || remainingHull < 0) {
            throw new IllegalArgumentException("Hull values cannot be negative");
        }
        destroyed = remainingHull == 0;
    }
}
