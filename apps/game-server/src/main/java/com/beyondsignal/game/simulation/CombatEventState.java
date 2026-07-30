package com.beyondsignal.game.simulation;

import java.util.Objects;
import java.util.UUID;

public record CombatEventState(
    long sequence,
    UUID targetId,
    String weapon,
    int shieldDamage,
    int hullDamage,
    boolean targetDestroyed
) {
    public CombatEventState {
        if (sequence <= 0) {
            throw new IllegalArgumentException("sequence must be positive");
        }
        Objects.requireNonNull(targetId, "targetId must not be null");
        weapon = Objects.requireNonNull(weapon, "weapon must not be null").trim();
        if (weapon.isEmpty()) {
            throw new IllegalArgumentException("weapon must not be blank");
        }
        if (shieldDamage < 0 || hullDamage < 0 || shieldDamage + hullDamage == 0) {
            throw new IllegalArgumentException("combat event must contain positive damage");
        }
    }
}
