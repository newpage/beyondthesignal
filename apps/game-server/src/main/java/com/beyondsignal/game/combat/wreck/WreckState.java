package com.beyondsignal.game.combat.wreck;

import com.beyondsignal.game.combat.model.CombatSide;
import java.util.Objects;
import java.util.UUID;

public record WreckState(
    UUID wreckId,
    UUID formerParticipantId,
    CombatSide side,
    long destroyedTick,
    String cause
) {
    public WreckState {
        wreckId = Objects.requireNonNull(wreckId, "wreckId");
        formerParticipantId = Objects.requireNonNull(
            formerParticipantId,
            "formerParticipantId"
        );
        side = Objects.requireNonNull(side, "side");
        if (destroyedTick < 0) {
            throw new IllegalArgumentException("destroyedTick cannot be negative");
        }
        if (cause == null || cause.isBlank()) {
            throw new IllegalArgumentException("cause is required");
        }
    }
}
