package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record FormationOrderSet(
    UUID fleetId,
    FormationObjective objective,
    List<FormationOrder> orders
) {
    public FormationOrderSet {
        fleetId = Objects.requireNonNull(fleetId, "fleetId");
        objective = Objects.requireNonNull(objective, "objective");
        orders = List.copyOf(Objects.requireNonNull(orders, "orders"));
    }
}
