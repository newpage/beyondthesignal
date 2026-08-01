package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.Objects;
import java.util.UUID;

public record FormationAssignment(
    UUID participantId,
    FormationSlot slot,
    FormationVector desiredPosition
) {
    public FormationAssignment {
        participantId = Objects.requireNonNull(participantId, "participantId");
        slot = Objects.requireNonNull(slot, "slot");
        desiredPosition = Objects.requireNonNull(desiredPosition, "desiredPosition");
    }
}
