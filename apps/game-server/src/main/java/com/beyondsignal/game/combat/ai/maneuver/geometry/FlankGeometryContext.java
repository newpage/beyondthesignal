package com.beyondsignal.game.combat.ai.maneuver.geometry;

import java.util.Objects;

public record FlankGeometryContext(
    CombatVector attackerPosition,
    CombatVector targetPosition,
    CombatVector targetForward,
    CombatVector targetRight,
    double desiredRange
) {
    public FlankGeometryContext {
        attackerPosition = Objects.requireNonNull(
            attackerPosition,
            "attackerPosition"
        );
        targetPosition = Objects.requireNonNull(
            targetPosition,
            "targetPosition"
        );
        targetForward = Objects.requireNonNull(
            targetForward,
            "targetForward"
        );
        targetRight = Objects.requireNonNull(
            targetRight,
            "targetRight"
        );
        if (!Double.isFinite(desiredRange) || desiredRange <= 0.0) {
            throw new IllegalArgumentException("desiredRange must be positive");
        }
    }
}
