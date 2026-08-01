package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.Objects;
import java.util.UUID;

public record FormationMemberState(
    UUID participantId,
    FormationVector currentPosition,
    FormationVector desiredPosition,
    double tolerance
) {
    public FormationMemberState {
        participantId = Objects.requireNonNull(participantId, "participantId");
        currentPosition = Objects.requireNonNull(currentPosition, "currentPosition");
        desiredPosition = Objects.requireNonNull(desiredPosition, "desiredPosition");
        if (!Double.isFinite(tolerance) || tolerance < 0.0) {
            throw new IllegalArgumentException("tolerance must be non-negative");
        }
    }

    public double driftDistance() {
        return currentPosition.distanceTo(desiredPosition);
    }

    public boolean inPosition() {
        return driftDistance() <= tolerance;
    }
}
