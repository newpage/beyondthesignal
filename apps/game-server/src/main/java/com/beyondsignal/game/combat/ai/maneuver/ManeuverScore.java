package com.beyondsignal.game.combat.ai.maneuver;

import java.util.Map;
import java.util.Objects;

public record ManeuverScore(
    ManeuverType type,
    int total,
    Map<String, Integer> factors
) implements Comparable<ManeuverScore> {
    public ManeuverScore {
        type = Objects.requireNonNull(type, "type");
        factors = Map.copyOf(Objects.requireNonNull(factors, "factors"));
        int calculated = factors.values().stream().mapToInt(Integer::intValue).sum();
        if (calculated != total) {
            throw new IllegalArgumentException("Maneuver score does not match factors");
        }
    }

    @Override
    public int compareTo(ManeuverScore other) {
        int scoreOrder = Integer.compare(other.total, total);
        return scoreOrder != 0 ? scoreOrder : type.compareTo(other.type);
    }
}
