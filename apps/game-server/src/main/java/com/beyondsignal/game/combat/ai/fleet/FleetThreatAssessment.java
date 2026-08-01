package com.beyondsignal.game.combat.ai.fleet;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record FleetThreatAssessment(Map<UUID, Integer> threatByTarget) {
    public FleetThreatAssessment {
        threatByTarget = Map.copyOf(
            Objects.requireNonNull(threatByTarget, "threatByTarget")
        );
    }

    public Optional<UUID> highestThreatTarget() {
        return threatByTarget.entrySet().stream()
            .sorted((left, right) -> {
                int score = Integer.compare(right.getValue(), left.getValue());
                return score != 0
                    ? score
                    : left.getKey().compareTo(right.getKey());
            })
            .map(Map.Entry::getKey)
            .findFirst();
    }
}
