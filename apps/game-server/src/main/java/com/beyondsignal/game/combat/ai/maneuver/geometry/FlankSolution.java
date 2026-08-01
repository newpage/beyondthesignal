package com.beyondsignal.game.combat.ai.maneuver.geometry;

import java.util.Map;
import java.util.Objects;

public record FlankSolution(
    FlankSide side,
    CombatVector destination,
    CombatVector approachVector,
    double travelDistance,
    int score,
    Map<String, Integer> factors
) implements Comparable<FlankSolution> {
    public FlankSolution {
        side = Objects.requireNonNull(side, "side");
        destination = Objects.requireNonNull(destination, "destination");
        approachVector = Objects.requireNonNull(approachVector, "approachVector");
        if (!Double.isFinite(travelDistance) || travelDistance < 0.0) {
            throw new IllegalArgumentException(
                "travelDistance must be non-negative"
            );
        }
        factors = Map.copyOf(Objects.requireNonNull(factors, "factors"));
        int calculated = factors.values().stream().mapToInt(Integer::intValue).sum();
        if (calculated != score) {
            throw new IllegalArgumentException("Flank score does not match factors");
        }
    }

    @Override
    public int compareTo(FlankSolution other) {
        int scoreOrder = Integer.compare(other.score, score);
        return scoreOrder != 0
            ? scoreOrder
            : side.compareTo(other.side);
    }
}
