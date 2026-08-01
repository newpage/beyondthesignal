package com.beyondsignal.game.combat.ai.personality;

public record AiPersonalityProfile(
    double aggression,
    double selfPreservation,
    double focusFire,
    double retreatHullThreshold,
    double retreatShieldThreshold
) {
    public AiPersonalityProfile {
        validateUnit(aggression, "aggression");
        validateUnit(selfPreservation, "selfPreservation");
        validateUnit(focusFire, "focusFire");
        validateUnit(retreatHullThreshold, "retreatHullThreshold");
        validateUnit(retreatShieldThreshold, "retreatShieldThreshold");
    }

    public static AiPersonalityProfile forPersonality(AiPersonality personality) {
        return switch (personality) {
            case AGGRESSIVE -> new AiPersonalityProfile(1.0, 0.15, 0.85, 0.10, 0.05);
            case BALANCED -> new AiPersonalityProfile(0.65, 0.55, 0.65, 0.25, 0.15);
            case DEFENSIVE -> new AiPersonalityProfile(0.35, 0.85, 0.45, 0.40, 0.30);
            case COWARDLY -> new AiPersonalityProfile(0.20, 1.0, 0.25, 0.60, 0.50);
            case ESCORT -> new AiPersonalityProfile(0.55, 0.70, 0.90, 0.30, 0.20);
        };
    }

    private static void validateUnit(double value, String name) {
        if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
    }
}
