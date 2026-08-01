package com.beyondsignal.game.combat.ai.maneuver.execution;

import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.Objects;

public final class HeadingController {
    public CombatVector turnToward(
        CombatVector currentHeading,
        CombatVector desiredHeading,
        double maximumTurnRateDegrees
    ) {
        Objects.requireNonNull(currentHeading, "currentHeading");
        Objects.requireNonNull(desiredHeading, "desiredHeading");
        if (!Double.isFinite(maximumTurnRateDegrees)
            || maximumTurnRateDegrees <= 0.0) {
            throw new IllegalArgumentException(
                "maximumTurnRateDegrees must be positive"
            );
        }

        CombatVector current = currentHeading.normalize();
        CombatVector desired = desiredHeading.normalize();
        double dot = clamp(current.dot(desired));
        double angle = Math.toDegrees(Math.acos(dot));

        if (angle <= maximumTurnRateDegrees) {
            return desired;
        }

        double ratio = maximumTurnRateDegrees / angle;
        CombatVector blended = current.scale(1.0 - ratio).add(desired.scale(ratio));
        return blended.normalize();
    }

    public double headingErrorDegrees(
        CombatVector currentHeading,
        CombatVector desiredHeading
    ) {
        double dot = clamp(
            currentHeading.normalize().dot(desiredHeading.normalize())
        );
        return Math.toDegrees(Math.acos(dot));
    }

    private static double clamp(double value) {
        return Math.max(-1.0, Math.min(1.0, value));
    }
}
