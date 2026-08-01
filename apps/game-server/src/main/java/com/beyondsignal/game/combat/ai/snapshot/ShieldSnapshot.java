package com.beyondsignal.game.combat.ai.snapshot;

import com.beyondsignal.game.combat.shield.ShieldQuadrant;
import java.util.Map;
import java.util.Objects;

public record ShieldSnapshot(
    Map<ShieldQuadrant, Integer> strength,
    Map<ShieldQuadrant, Integer> maximumStrength
) {
    public ShieldSnapshot {
        strength = Map.copyOf(Objects.requireNonNull(strength, "strength"));
        maximumStrength = Map.copyOf(
            Objects.requireNonNull(maximumStrength, "maximumStrength")
        );
        for (ShieldQuadrant quadrant : ShieldQuadrant.values()) {
            if (!strength.containsKey(quadrant) || !maximumStrength.containsKey(quadrant)) {
                throw new IllegalArgumentException("Missing shield quadrant: " + quadrant);
            }
        }
    }

    public int totalStrength() {
        return strength.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int totalMaximumStrength() {
        return maximumStrength.values().stream().mapToInt(Integer::intValue).sum();
    }

    public double percentage() {
        int maximum = totalMaximumStrength();
        return maximum == 0 ? 0.0 : totalStrength() / (double) maximum;
    }
}
