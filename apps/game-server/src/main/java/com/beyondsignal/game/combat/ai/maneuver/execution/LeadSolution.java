package com.beyondsignal.game.combat.ai.maneuver.execution;

import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.Objects;

public record LeadSolution(
    CombatVector aimPoint,
    CombatVector aimDirection,
    double timeToImpact,
    boolean feasible
) {
    public LeadSolution {
        aimPoint = Objects.requireNonNull(aimPoint, "aimPoint");
        aimDirection = Objects.requireNonNull(aimDirection, "aimDirection");
        if (!Double.isFinite(timeToImpact) || timeToImpact < 0.0) {
            throw new IllegalArgumentException(
                "timeToImpact must be non-negative"
            );
        }
    }
}
