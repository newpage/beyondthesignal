package com.beyondsignal.game.combat.ai.maneuver.execution;

import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record PursuitPlan(
    UUID pursuerId,
    UUID targetId,
    PursuitState state,
    CombatVector interceptPoint,
    CombatVector pursuitVector,
    double timeToIntercept,
    double desiredClosingSpeed,
    int confidence,
    Map<String, Integer> reasons
) {
    public PursuitPlan {
        pursuerId = Objects.requireNonNull(pursuerId, "pursuerId");
        targetId = Objects.requireNonNull(targetId, "targetId");
        state = Objects.requireNonNull(state, "state");
        interceptPoint = Objects.requireNonNull(interceptPoint, "interceptPoint");
        pursuitVector = Objects.requireNonNull(pursuitVector, "pursuitVector");
        if (!Double.isFinite(timeToIntercept) || timeToIntercept < 0.0) {
            throw new IllegalArgumentException(
                "timeToIntercept must be non-negative"
            );
        }
        if (!Double.isFinite(desiredClosingSpeed)
            || desiredClosingSpeed < 0.0) {
            throw new IllegalArgumentException(
                "desiredClosingSpeed must be non-negative"
            );
        }
        if (confidence < 0 || confidence > 100) {
            throw new IllegalArgumentException(
                "confidence must be between 0 and 100"
            );
        }
        reasons = Map.copyOf(Objects.requireNonNull(reasons, "reasons"));
    }
}
