package com.beyondsignal.game.combat.ai.maneuver;

import java.util.List;
import java.util.Objects;

public record ManeuverDecision(
    ManeuverType selected,
    ManeuverObjective objective,
    List<ManeuverScore> rankedCandidates,
    int confidence
) {
    public ManeuverDecision {
        selected = Objects.requireNonNull(selected, "selected");
        objective = Objects.requireNonNull(objective, "objective");
        rankedCandidates = List.copyOf(
            Objects.requireNonNull(rankedCandidates, "rankedCandidates")
        );
        if (confidence < 0 || confidence > 100) {
            throw new IllegalArgumentException("confidence must be between 0 and 100");
        }
    }

    public ManeuverScore selectedScore() {
        return rankedCandidates.stream()
            .filter(score -> score.type() == selected)
            .findFirst()
            .orElseThrow();
    }
}
