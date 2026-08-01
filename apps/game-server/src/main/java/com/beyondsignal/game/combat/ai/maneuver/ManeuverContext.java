package com.beyondsignal.game.combat.ai.maneuver;

import java.util.Objects;
import java.util.UUID;

public record ManeuverContext(
    UUID participantId,
    UUID targetId,
    double distanceToTarget,
    double optimalWeaponRange,
    double hullPercentage,
    double shieldPercentage,
    double formationIntegrity,
    double incomingThreat,
    double friendlySupport,
    ManeuverObjective objective,
    ManeuverConstraints constraints
) {
    public ManeuverContext {
        participantId = Objects.requireNonNull(participantId, "participantId");
        targetId = Objects.requireNonNull(targetId, "targetId");
        objective = Objects.requireNonNull(objective, "objective");
        constraints = Objects.requireNonNull(constraints, "constraints");

        if (!Double.isFinite(distanceToTarget) || distanceToTarget < 0.0) {
            throw new IllegalArgumentException("distanceToTarget must be non-negative");
        }
        if (!Double.isFinite(optimalWeaponRange) || optimalWeaponRange <= 0.0) {
            throw new IllegalArgumentException("optimalWeaponRange must be positive");
        }

        validateUnit(hullPercentage, "hullPercentage");
        validateUnit(shieldPercentage, "shieldPercentage");
        validateUnit(formationIntegrity, "formationIntegrity");
        validateUnit(incomingThreat, "incomingThreat");
        validateUnit(friendlySupport, "friendlySupport");
    }

    public double normalizedDistance() {
        return distanceToTarget / optimalWeaponRange;
    }

    private static void validateUnit(double value, String name) {
        if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
    }
}
