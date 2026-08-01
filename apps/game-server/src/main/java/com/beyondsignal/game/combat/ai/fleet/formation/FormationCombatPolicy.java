package com.beyondsignal.game.combat.ai.fleet.formation;

public record FormationCombatPolicy(
    double breakHullThreshold,
    double breakShieldThreshold,
    double rejoinHullThreshold,
    double rejoinShieldThreshold,
    boolean protectCommander,
    boolean allowEvasiveBreak
) {
    public FormationCombatPolicy {
        validateUnit(breakHullThreshold, "breakHullThreshold");
        validateUnit(breakShieldThreshold, "breakShieldThreshold");
        validateUnit(rejoinHullThreshold, "rejoinHullThreshold");
        validateUnit(rejoinShieldThreshold, "rejoinShieldThreshold");

        if (rejoinHullThreshold < breakHullThreshold) {
            throw new IllegalArgumentException(
                "rejoinHullThreshold cannot be below breakHullThreshold"
            );
        }
        if (rejoinShieldThreshold < breakShieldThreshold) {
            throw new IllegalArgumentException(
                "rejoinShieldThreshold cannot be below breakShieldThreshold"
            );
        }
    }

    public static FormationCombatPolicy standard() {
        return new FormationCombatPolicy(
            0.20,
            0.10,
            0.45,
            0.35,
            true,
            true
        );
    }

    private static void validateUnit(double value, String name) {
        if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
    }
}
