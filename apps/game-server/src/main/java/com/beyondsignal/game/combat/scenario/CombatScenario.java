package com.beyondsignal.game.combat.scenario;

import com.beyondsignal.game.combat.model.CombatId;
import java.util.List;
import java.util.Objects;

public record CombatScenario(
    CombatId combatId,
    long seed,
    int maximumTicks,
    double engagementDistance,
    List<CombatScenarioParticipant> participants
) {
    public CombatScenario {
        combatId = Objects.requireNonNull(combatId, "combatId");
        if (maximumTicks < 1) {
            throw new IllegalArgumentException("maximumTicks must be positive");
        }
        if (!Double.isFinite(engagementDistance) || engagementDistance < 0.0) {
            throw new IllegalArgumentException("engagementDistance must be non-negative");
        }
        participants = List.copyOf(Objects.requireNonNull(participants, "participants"));
        if (participants.size() < 2) {
            throw new IllegalArgumentException("Scenario requires at least two participants");
        }
    }
}
