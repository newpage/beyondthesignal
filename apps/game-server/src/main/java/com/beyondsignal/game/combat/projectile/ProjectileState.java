package com.beyondsignal.game.combat.projectile;

import com.beyondsignal.game.combat.weapon.DamageType;
import java.util.Objects;
import java.util.UUID;

public record ProjectileState(
    UUID projectileId,
    UUID sourceId,
    UUID targetId,
    String weaponId,
    DamageType damageType,
    int baseDamage,
    long launchTick,
    long impactTick,
    boolean predictedHit,
    ProjectileStatus status
) {
    public ProjectileState {
        projectileId = Objects.requireNonNull(projectileId, "projectileId");
        sourceId = Objects.requireNonNull(sourceId, "sourceId");
        targetId = Objects.requireNonNull(targetId, "targetId");
        if (weaponId == null || weaponId.isBlank()) {
            throw new IllegalArgumentException("weaponId is required");
        }
        damageType = Objects.requireNonNull(damageType, "damageType");
        if (baseDamage <= 0) {
            throw new IllegalArgumentException("baseDamage must be positive");
        }
        if (launchTick < 0 || impactTick <= launchTick) {
            throw new IllegalArgumentException(
                "impactTick must be after launchTick"
            );
        }
        status = Objects.requireNonNull(status, "status");
    }

    public static ProjectileState inFlight(
        UUID projectileId,
        UUID sourceId,
        UUID targetId,
        String weaponId,
        DamageType damageType,
        int baseDamage,
        long launchTick,
        long impactTick,
        boolean predictedHit
    ) {
        return new ProjectileState(
            projectileId,
            sourceId,
            targetId,
            weaponId,
            damageType,
            baseDamage,
            launchTick,
            impactTick,
            predictedHit,
            ProjectileStatus.IN_FLIGHT
        );
    }

    public boolean due(long tick) {
        return status == ProjectileStatus.IN_FLIGHT && tick >= impactTick;
    }

    public double progress(long tick) {
        double elapsed = tick - launchTick;
        double duration = impactTick - launchTick;
        return Math.max(0.0, Math.min(1.0, elapsed / duration));
    }

    public ProjectileState withStatus(ProjectileStatus value) {
        return new ProjectileState(
            projectileId,
            sourceId,
            targetId,
            weaponId,
            damageType,
            baseDamage,
            launchTick,
            impactTick,
            predictedHit,
            value
        );
    }
}
