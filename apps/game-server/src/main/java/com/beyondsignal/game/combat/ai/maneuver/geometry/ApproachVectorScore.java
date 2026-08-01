package com.beyondsignal.game.combat.ai.maneuver.geometry;

import java.util.Map;
import java.util.Objects;

public record ApproachVectorScore(
    CombatVector vector,
    int score,
    Map<String, Integer> factors
) {
    public ApproachVectorScore {
        vector = Objects.requireNonNull(vector, "vector");
        factors = Map.copyOf(Objects.requireNonNull(factors, "factors"));
        int calculated = factors.values().stream().mapToInt(Integer::intValue).sum();
        if (calculated != score) {
            throw new IllegalArgumentException(
                "Approach vector score does not match factors"
            );
        }
    }
}
