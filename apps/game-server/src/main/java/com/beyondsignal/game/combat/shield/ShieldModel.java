package com.beyondsignal.game.combat.shield;

import com.beyondsignal.game.combat.weapon.DamageType;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class ShieldModel {
    private final EnumMap<ShieldQuadrant, ShieldQuadrantState> quadrants =
        new EnumMap<>(ShieldQuadrant.class);

    public ShieldModel(Map<ShieldQuadrant, ShieldQuadrantState> initialState) {
        Objects.requireNonNull(initialState, "initialState");
        for (ShieldQuadrant quadrant : ShieldQuadrant.values()) {
            ShieldQuadrantState state = initialState.get(quadrant);
            if (state == null) {
                throw new IllegalArgumentException("Missing shield quadrant: " + quadrant);
            }
            quadrants.put(quadrant, state);
        }
    }

    public static ShieldModel uniform(int maximumStrength, int regenerationPerTick) {
        EnumMap<ShieldQuadrant, ShieldQuadrantState> states =
            new EnumMap<>(ShieldQuadrant.class);
        for (ShieldQuadrant quadrant : ShieldQuadrant.values()) {
            states.put(
                quadrant,
                ShieldQuadrantState.full(maximumStrength, regenerationPerTick)
            );
        }
        return new ShieldModel(states);
    }

    public synchronized ShieldImpact apply(
        ShieldQuadrant quadrant,
        DamageType damageType,
        int incomingDamage,
        double resistanceModifier
    ) {
        Objects.requireNonNull(quadrant, "quadrant");
        Objects.requireNonNull(damageType, "damageType");
        if (incomingDamage < 0) {
            throw new IllegalArgumentException("incomingDamage cannot be negative");
        }
        if (!Double.isFinite(resistanceModifier) || resistanceModifier < 0.0) {
            throw new IllegalArgumentException("resistanceModifier must be non-negative");
        }

        ShieldQuadrantState state = quadrants.get(quadrant);
        int effectiveDamage = (int) Math.ceil(incomingDamage * resistanceModifier);
        int absorbed = Math.min(state.strength(), effectiveDamage);
        int penetrating = Math.max(0, effectiveDamage - absorbed);

        quadrants.put(quadrant, state.withStrength(state.strength() - absorbed));

        return new ShieldImpact(
            quadrant,
            damageType,
            effectiveDamage,
            absorbed,
            penetrating,
            quadrants.get(quadrant).collapsed()
        );
    }

    public synchronized void regenerate() {
        for (ShieldQuadrant quadrant : ShieldQuadrant.values()) {
            quadrants.put(quadrant, quadrants.get(quadrant).regenerate());
        }
    }

    public synchronized ShieldQuadrantState state(ShieldQuadrant quadrant) {
        return quadrants.get(quadrant);
    }

    public synchronized Map<ShieldQuadrant, ShieldQuadrantState> snapshot() {
        return Map.copyOf(quadrants);
    }
}
