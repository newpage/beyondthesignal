package com.beyondsignal.game.combat.projectile;

import com.beyondsignal.game.combat.engine.CombatEncounter;

@FunctionalInterface
public interface ProjectileInterceptor {
    boolean intercept(
        CombatEncounter encounter,
        ProjectileState projectile,
        long tick
    );

    static ProjectileInterceptor none() {
        return (encounter, projectile, tick) -> false;
    }
}
