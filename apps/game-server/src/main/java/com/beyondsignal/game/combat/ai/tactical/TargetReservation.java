package com.beyondsignal.game.combat.ai.tactical;

import java.util.Objects;
import java.util.UUID;

public record TargetReservation(
    UUID attackerId,
    UUID targetId,
    int reservedDamage
) {
    public TargetReservation {
        attackerId = Objects.requireNonNull(attackerId, "attackerId");
        targetId = Objects.requireNonNull(targetId, "targetId");
        if (reservedDamage < 0) {
            throw new IllegalArgumentException("reservedDamage cannot be negative");
        }
    }
}
