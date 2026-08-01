package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.List;
import java.util.Objects;

public record FormationLifecycleUpdate(
    FormationPlan activePlan,
    FormationUpdate formationUpdate,
    List<FormationCombatDecision> combatDecisions,
    List<FormationProtectionOrder> protectionOrders
) {
    public FormationLifecycleUpdate {
        activePlan = Objects.requireNonNull(activePlan, "activePlan");
        formationUpdate = Objects.requireNonNull(formationUpdate, "formationUpdate");
        combatDecisions = List.copyOf(
            Objects.requireNonNull(combatDecisions, "combatDecisions")
        );
        protectionOrders = List.copyOf(
            Objects.requireNonNull(protectionOrders, "protectionOrders")
        );
    }
}
