package com.beyondsignal.game.combat.ai.fleet;

import java.util.Objects;
import java.util.UUID;

public record FleetOrder(
    UUID participantId,
    FleetObjective objective
) {
    public FleetOrder {
        participantId = Objects.requireNonNull(participantId, "participantId");
        objective = Objects.requireNonNull(objective, "objective");
    }
}
