package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.Objects;
import java.util.UUID;

public record FormationProtectionOrder(
    UUID protectorId,
    UUID commanderId,
    FormationVector desiredPosition,
    int priority
) {
    public FormationProtectionOrder {
        protectorId = Objects.requireNonNull(protectorId, "protectorId");
        commanderId = Objects.requireNonNull(commanderId, "commanderId");
        desiredPosition = Objects.requireNonNull(desiredPosition, "desiredPosition");
        if (priority < 0 || priority > 100) {
            throw new IllegalArgumentException("priority must be between 0 and 100");
        }
    }
}
