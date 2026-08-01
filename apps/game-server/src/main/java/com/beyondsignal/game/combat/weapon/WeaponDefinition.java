package com.beyondsignal.game.combat.weapon;

import java.util.Objects;

public record WeaponDefinition(
    String id,
    String displayName,
    WeaponType type,
    DamageType damageType,
    WeaponArc arc,
    int baseDamage,
    double maximumRange,
    double baseAccuracy,
    int chargeTicks,
    int cooldownTicks,
    int ammunitionPerShot
) {
    public WeaponDefinition {
        id = requireText(id, "id");
        displayName = requireText(displayName, "displayName");
        type = Objects.requireNonNull(type, "type");
        damageType = Objects.requireNonNull(damageType, "damageType");
        arc = Objects.requireNonNull(arc, "arc");
        if (baseDamage < 1) {
            throw new IllegalArgumentException("baseDamage must be positive");
        }
        if (!Double.isFinite(maximumRange) || maximumRange <= 0.0) {
            throw new IllegalArgumentException("maximumRange must be positive");
        }
        if (!Double.isFinite(baseAccuracy) || baseAccuracy < 0.0 || baseAccuracy > 1.0) {
            throw new IllegalArgumentException("baseAccuracy must be between 0.0 and 1.0");
        }
        if (chargeTicks < 0 || cooldownTicks < 0 || ammunitionPerShot < 0) {
            throw new IllegalArgumentException("Tick and ammunition values cannot be negative");
        }
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value;
    }
}
