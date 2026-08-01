package com.beyondsignal.game.combat.ai.tactical;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record TacticalTargetScore(
    UUID targetId,
    int score,
    Map<String, Integer> factors
) implements Comparable<TacticalTargetScore> {
    public TacticalTargetScore {
        targetId = Objects.requireNonNull(targetId, "targetId");
        factors = Map.copyOf(Objects.requireNonNull(factors, "factors"));
        int calculated = factors.values().stream().mapToInt(Integer::intValue).sum();
        if (calculated != score) {
            throw new IllegalArgumentException("Target score does not match factors");
        }
    }

    @Override
    public int compareTo(TacticalTargetScore other) {
        int scoreOrder = Integer.compare(other.score, score);
        return scoreOrder != 0
            ? scoreOrder
            : targetId.compareTo(other.targetId);
    }
}
