package com.beyondsignal.game.combat.ai.maneuver.execution;

import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.Objects;

public final class TangentialVelocityCalculator {
    public CombatVector calculate(
        CombatVector radialDirection,
        CombatVector orbitNormal,
        OrbitDirection direction,
        double speed
    ) {
        Objects.requireNonNull(radialDirection, "radialDirection");
        Objects.requireNonNull(orbitNormal, "orbitNormal");
        Objects.requireNonNull(direction, "direction");

        if (!Double.isFinite(speed) || speed <= 0.0) {
            throw new IllegalArgumentException("speed must be positive");
        }

        CombatVector tangent = orbitNormal.normalize()
            .cross(radialDirection.normalize())
            .normalize();

        if (direction == OrbitDirection.CLOCKWISE) {
            tangent = tangent.scale(-1.0);
        }

        return tangent.scale(speed);
    }
}
