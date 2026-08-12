package com.beyondsignal.game.presentation.frame;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record BattleFleetDecisionView(
    UUID fleetId,
    String doctrine,
    String objective,
    UUID primaryTargetId,
    List<BattleThreatView> threats,
    String commanderStatus,
    boolean retreat,
    long generatedTick
) {
    public BattleFleetDecisionView {
        fleetId = Objects.requireNonNull(fleetId, "fleetId");
        doctrine = Objects.requireNonNull(doctrine, "doctrine");
        objective = Objects.requireNonNull(objective, "objective");
        threats = List.copyOf(Objects.requireNonNull(threats, "threats"));
        commanderStatus = Objects.requireNonNull(
            commanderStatus,
            "commanderStatus"
        );
    }
}
