package com.beyondsignal.game.combat.ai.fleet;

import java.util.List;
import java.util.Objects;

public record FleetPlan(
    FleetObjective objective,
    List<FleetOrder> orders
) {
    public FleetPlan {
        objective = Objects.requireNonNull(objective, "objective");
        orders = List.copyOf(Objects.requireNonNull(orders, "orders"));
    }
}
