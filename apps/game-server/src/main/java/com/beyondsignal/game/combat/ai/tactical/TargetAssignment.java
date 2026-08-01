package com.beyondsignal.game.combat.ai.tactical;

import java.util.Objects;
import java.util.UUID;

public record TargetAssignment(
    UUID attackerId,
    UUID targetId,
    int expectedDamage,
    int targetScore
) {
    public TargetAssignment {
        attackerId = Objects.requireNonNull(attackerId, "attackerId");
        targetId = Objects.requireNonNull(targetId, "targetId");
        if (expectedDamage < 0 || targetScore < 0) {
            throw new IllegalArgumentException("Damage and score cannot be negative");
        }
    }
}
