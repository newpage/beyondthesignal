package com.beyondsignal.game.combat.ai.tactical;

import java.util.Objects;
import java.util.UUID;

public record TacticalTargetProfile(
    UUID targetId,
    TargetPriorityClass priorityClass,
    double hullPercentage,
    double shieldPercentage,
    int readyWeaponDamage,
    boolean targetingFleetMember,
    boolean commander,
    boolean supportAsset
) {
    public TacticalTargetProfile {
        targetId = Objects.requireNonNull(targetId, "targetId");
        priorityClass = Objects.requireNonNull(priorityClass, "priorityClass");
        validateUnit(hullPercentage, "hullPercentage");
        validateUnit(shieldPercentage, "shieldPercentage");
        if (readyWeaponDamage < 0) {
            throw new IllegalArgumentException("readyWeaponDamage cannot be negative");
        }
    }

    private static void validateUnit(double value, String name) {
        if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
    }
}
