package com.beyondsignal.game.combat.ai.snapshot;

import com.beyondsignal.game.combat.weapon.WeaponType;
import java.util.Objects;
import java.util.UUID;

public record WeaponSnapshot(
    UUID mountId,
    String weaponId,
    WeaponType type,
    boolean ready,
    int cooldownRemainingTicks,
    int ammunitionRemaining,
    int baseDamage,
    double maximumRange
) {
    public WeaponSnapshot {
        mountId = Objects.requireNonNull(mountId, "mountId");
        if (weaponId == null || weaponId.isBlank()) {
            throw new IllegalArgumentException("weaponId is required");
        }
        type = Objects.requireNonNull(type, "type");
        if (cooldownRemainingTicks < 0 || ammunitionRemaining < 0 || baseDamage < 1) {
            throw new IllegalArgumentException("Invalid weapon snapshot values");
        }
        if (!Double.isFinite(maximumRange) || maximumRange <= 0.0) {
            throw new IllegalArgumentException("maximumRange must be positive");
        }
    }
}
