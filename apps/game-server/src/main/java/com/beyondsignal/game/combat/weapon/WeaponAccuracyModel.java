package com.beyondsignal.game.combat.weapon;

public final class WeaponAccuracyModel {
    public double hitProbability(
        WeaponDefinition weapon,
        double distance,
        double targetingQuality,
        double evasion,
        double attackerPowerModifier
    ) {
        if (!Double.isFinite(distance) || distance < 0.0) {
            throw new IllegalArgumentException("distance must be finite and non-negative");
        }
        validateUnit(targetingQuality, "targetingQuality");
        validateUnit(evasion, "evasion");
        if (!Double.isFinite(attackerPowerModifier) || attackerPowerModifier <= 0.0) {
            throw new IllegalArgumentException("attackerPowerModifier must be positive");
        }

        if (distance > weapon.maximumRange()) {
            return 0.0;
        }

        double rangeFactor = 1.0 - (distance / weapon.maximumRange()) * 0.45;
        double value = weapon.baseAccuracy()
            * rangeFactor
            * (0.5 + targetingQuality * 0.5)
            * (1.0 - evasion * 0.65)
            * attackerPowerModifier;

        return clamp(value);
    }

    private static void validateUnit(double value, String name) {
        if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(name + " must be between 0.0 and 1.0");
        }
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
