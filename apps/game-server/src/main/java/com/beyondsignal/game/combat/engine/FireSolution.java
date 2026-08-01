package com.beyondsignal.game.combat.engine;

import com.beyondsignal.game.combat.weapon.WeaponMountState;
import java.util.Objects;
import java.util.UUID;

public record FireSolution(
    UUID attackerId,
    UUID targetId,
    WeaponMountState weapon,
    double distance,
    double hitProbability,
    double roll,
    boolean hit
) {
    public FireSolution {
        attackerId = Objects.requireNonNull(attackerId, "attackerId");
        targetId = Objects.requireNonNull(targetId, "targetId");
        weapon = Objects.requireNonNull(weapon, "weapon");
        if (!Double.isFinite(distance) || distance < 0.0) {
            throw new IllegalArgumentException("distance must be finite and non-negative");
        }
        if (!Double.isFinite(hitProbability) || hitProbability < 0.0 || hitProbability > 1.0) {
            throw new IllegalArgumentException("hitProbability must be between 0 and 1");
        }
        if (!Double.isFinite(roll) || roll < 0.0 || roll >= 1.0) {
            throw new IllegalArgumentException(
                "roll must be between 0 inclusive and 1 exclusive"
            );
        }
        hit = roll < hitProbability;
    }
}
