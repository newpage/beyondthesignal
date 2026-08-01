package com.beyondsignal.game.combat.ai.tactical;

import java.util.Objects;
import java.util.UUID;

public record AttackerProfile(
    UUID attackerId,
    int availableDamage,
    boolean escort,
    boolean commander
) {
    public AttackerProfile {
        attackerId = Objects.requireNonNull(attackerId, "attackerId");
        if (availableDamage < 0) {
            throw new IllegalArgumentException("availableDamage cannot be negative");
        }
    }
}
