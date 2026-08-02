package com.beyondsignal.game.combat.fleet.squadron;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record SquadronExecutionPlan(
    UUID fleetId,
    UUID squadronId,
    SquadronExecutionMode mode,
    UUID targetId,
    List<UUID> memberIds,
    double morale,
    long generatedTick
) {
    public SquadronExecutionPlan {
        fleetId = Objects.requireNonNull(fleetId, "fleetId");
        squadronId = Objects.requireNonNull(squadronId, "squadronId");
        mode = Objects.requireNonNull(mode, "mode");
        memberIds = List.copyOf(
            Objects.requireNonNull(memberIds, "memberIds")
        );
        if (!Double.isFinite(morale) || morale < 0.0 || morale > 1.0) {
            throw new IllegalArgumentException(
                "morale must be between 0 and 1"
            );
        }
        if (generatedTick < 0) {
            throw new IllegalArgumentException(
                "generatedTick cannot be negative"
            );
        }
    }
}
