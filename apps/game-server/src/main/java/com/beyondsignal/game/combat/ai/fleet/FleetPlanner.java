package com.beyondsignal.game.combat.ai.fleet;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class FleetPlanner {
    public FleetPlan plan(
        FleetSnapshot snapshot,
        FleetObjective objective
    ) {
        Objects.requireNonNull(snapshot, "snapshot");
        Objects.requireNonNull(objective, "objective");

        List<FleetOrder> orders = snapshot.members().stream()
            .sorted(Comparator
                .comparingInt(FleetMember::commandPriority)
                .reversed()
                .thenComparing(FleetMember::participantId))
            .map(member -> new FleetOrder(member.participantId(), objective))
            .toList();

        return new FleetPlan(objective, orders);
    }
}
