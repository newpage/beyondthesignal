package com.beyondsignal.game.combat.shield;

public record ShieldQuadrantState(
    int strength,
    int maximumStrength,
    int regenerationPerTick,
    boolean enabled
) {
    public ShieldQuadrantState {
        if (maximumStrength < 1) {
            throw new IllegalArgumentException("maximumStrength must be positive");
        }
        if (strength < 0 || strength > maximumStrength) {
            throw new IllegalArgumentException("strength is outside valid range");
        }
        if (regenerationPerTick < 0) {
            throw new IllegalArgumentException("regenerationPerTick cannot be negative");
        }
    }

    public static ShieldQuadrantState full(int maximumStrength, int regenerationPerTick) {
        return new ShieldQuadrantState(
            maximumStrength,
            maximumStrength,
            regenerationPerTick,
            true
        );
    }

    public boolean collapsed() {
        return strength == 0;
    }

    public ShieldQuadrantState regenerate() {
        if (!enabled || strength == maximumStrength) {
            return this;
        }
        return new ShieldQuadrantState(
            Math.min(maximumStrength, strength + regenerationPerTick),
            maximumStrength,
            regenerationPerTick,
            enabled
        );
    }

    public ShieldQuadrantState withStrength(int nextStrength) {
        return new ShieldQuadrantState(
            nextStrength,
            maximumStrength,
            regenerationPerTick,
            enabled
        );
    }
}
