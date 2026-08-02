package com.beyondsignal.game.combat.projectile;

import com.beyondsignal.game.combat.engine.CombatEncounter;
import com.beyondsignal.game.combat.weapon.WeaponMountState;
import com.beyondsignal.game.combat.weapon.WeaponType;
import java.util.Comparator;
import java.util.Objects;

public final class PointDefenseProjectileInterceptor
    implements ProjectileInterceptor {

    private final double baseInterceptChance;

    public PointDefenseProjectileInterceptor() {
        this(0.55);
    }

    public PointDefenseProjectileInterceptor(double baseInterceptChance) {
        if (!Double.isFinite(baseInterceptChance)
            || baseInterceptChance < 0.0
            || baseInterceptChance > 1.0) {
            throw new IllegalArgumentException(
                "baseInterceptChance must be between 0 and 1"
            );
        }
        this.baseInterceptChance = baseInterceptChance;
    }

    @Override
    public boolean intercept(
        CombatEncounter encounter,
        ProjectileState projectile,
        long tick
    ) {
        Objects.requireNonNull(encounter, "encounter");
        Objects.requireNonNull(projectile, "projectile");

        var target = encounter.participant(projectile.targetId());
        if (target.isEmpty() || !target.get().operational()) {
            return false;
        }

        WeaponMountState pointDefense = target.get().weapons().stream()
            .filter(WeaponMountState::readyToFire)
            .filter(mount -> mount.definition().type() == WeaponType.BEAM)
            .sorted(Comparator.comparing(WeaponMountState::mountId))
            .findFirst()
            .orElse(null);

        if (pointDefense == null) {
            return false;
        }

        double qualityAdjustedChance = Math.min(
            0.95,
            baseInterceptChance
                + target.get().targetingQuality() * 0.25
                - projectile.progress(tick) * 0.20
        );

        long mixed = encounter.context().seed()
            ^ projectile.projectileId().getMostSignificantBits()
            ^ projectile.projectileId().getLeastSignificantBits()
            ^ target.get().participantId().getMostSignificantBits()
            ^ tick;
        double roll = ((mixed >>> 11) & ((1L << 53) - 1))
            / (double) (1L << 53);

        if (roll >= qualityAdjustedChance) {
            return false;
        }

        target.get().replaceWeapon(pointDefense.fire());
        return true;
    }
}
