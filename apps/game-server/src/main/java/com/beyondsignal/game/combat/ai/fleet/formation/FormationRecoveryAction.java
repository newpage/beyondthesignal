package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.Objects;
import java.util.UUID;

public record FormationRecoveryAction(
    UUID participantId,
    FormationVector desiredPosition,
    double driftDistance,
    int urgency
) {
    public FormationRecoveryAction {
        participantId = Objects.requireNonNull(participantId, "participantId");
        desiredPosition = Objects.requireNonNull(desiredPosition, "desiredPosition");
        if (!Double.isFinite(driftDistance) || driftDistance < 0.0) {
            throw new IllegalArgumentException("driftDistance must be non-negative");
        }
        if (urgency < 0 || urgency > 100) {
            throw new IllegalArgumentException("urgency must be between 0 and 100");
        }
    }
}
