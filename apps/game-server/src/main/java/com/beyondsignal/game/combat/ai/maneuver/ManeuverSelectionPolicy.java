package com.beyondsignal.game.combat.ai.maneuver;

public record ManeuverSelectionPolicy(
    double aggression,
    double caution,
    double formationDiscipline,
    double positionalPreference,
    double rangePreference
) {
    public ManeuverSelectionPolicy {
        validateUnit(aggression, "aggression");
        validateUnit(caution, "caution");
        validateUnit(formationDiscipline, "formationDiscipline");
        validateUnit(positionalPreference, "positionalPreference");
        validateUnit(rangePreference, "rangePreference");
    }

    public static ManeuverSelectionPolicy balanced() {
        return new ManeuverSelectionPolicy(0.5, 0.5, 0.7, 0.6, 0.6);
    }

    private static void validateUnit(double value, String name) {
        if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
    }
}
