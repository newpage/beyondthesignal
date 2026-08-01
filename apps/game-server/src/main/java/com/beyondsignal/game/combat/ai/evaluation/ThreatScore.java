package com.beyondsignal.game.combat.ai.evaluation;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record ThreatScore(
    UUID targetId,
    int total,
    Map<String, Integer> factors
) implements Comparable<ThreatScore> {
    public ThreatScore {
        targetId = Objects.requireNonNull(targetId, "targetId");
        factors = Map.copyOf(Objects.requireNonNull(factors, "factors"));
        int calculated = factors.values().stream().mapToInt(Integer::intValue).sum();
        if (calculated != total) {
            throw new IllegalArgumentException("Threat factor total does not match score");
        }
    }

    @Override
    public int compareTo(ThreatScore other) {
        int scoreComparison = Integer.compare(other.total, total);
        return scoreComparison != 0
            ? scoreComparison
            : targetId.compareTo(other.targetId);
    }
}
