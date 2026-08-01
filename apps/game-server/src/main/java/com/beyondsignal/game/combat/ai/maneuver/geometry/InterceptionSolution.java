package com.beyondsignal.game.combat.ai.maneuver.geometry;

import java.util.Objects;

public record InterceptionSolution(
    CombatVector interceptPoint,
    double timeToIntercept,
    CombatVector interceptVector,
    boolean feasible
) {
    public InterceptionSolution {
        interceptPoint = Objects.requireNonNull(
            interceptPoint,
            "interceptPoint"
        );
        interceptVector = Objects.requireNonNull(
            interceptVector,
            "interceptVector"
        );
        if (!Double.isFinite(timeToIntercept) || timeToIntercept < 0.0) {
            throw new IllegalArgumentException(
                "timeToIntercept must be non-negative"
            );
        }
    }
}
