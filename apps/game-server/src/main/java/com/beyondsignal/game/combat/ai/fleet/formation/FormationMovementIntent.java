package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.Objects;
import java.util.UUID;

public record FormationMovementIntent(
    UUID participantId,
    FormationVector desiredPosition,
    double currentDrift,
    int urgency,
    boolean holdPosition
) {
    public FormationMovementIntent {
        participantId = Objects.requireNonNull(participantId, "participantId");
        desiredPosition = Objects.requireNonNull(desiredPosition, "desiredPosition");
        if (!Double.isFinite(currentDrift) || currentDrift < 0.0) {
            throw new IllegalArgumentException("currentDrift must be non-negative");
        }
        if (urgency < 0 || urgency > 100) {
            throw new IllegalArgumentException("urgency must be between 0 and 100");
        }
    }
}
