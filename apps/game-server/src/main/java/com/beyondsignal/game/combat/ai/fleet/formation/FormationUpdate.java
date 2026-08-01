package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.List;
import java.util.Objects;

public record FormationUpdate(
    FormationState state,
    FormationObjective objective,
    FormationOrderSet orders,
    FormationRecoveryPlan recoveryPlan
) {
    public FormationUpdate {
        state = Objects.requireNonNull(state, "state");
        objective = Objects.requireNonNull(objective, "objective");
        orders = Objects.requireNonNull(orders, "orders");
        recoveryPlan = Objects.requireNonNull(recoveryPlan, "recoveryPlan");
    }

    public List<FormationMovementIntent> movementIntents() {
        return orders.orders().stream()
            .map(FormationOrder::movementIntent)
            .toList();
    }
}
