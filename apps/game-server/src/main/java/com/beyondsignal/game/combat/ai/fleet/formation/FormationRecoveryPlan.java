package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.List;
import java.util.Objects;

public record FormationRecoveryPlan(
    FormationStatus currentStatus,
    List<FormationRecoveryAction> actions
) {
    public FormationRecoveryPlan {
        currentStatus = Objects.requireNonNull(currentStatus, "currentStatus");
        actions = List.copyOf(Objects.requireNonNull(actions, "actions"));
    }

    public boolean recoveryRequired() {
        return !actions.isEmpty();
    }
}
