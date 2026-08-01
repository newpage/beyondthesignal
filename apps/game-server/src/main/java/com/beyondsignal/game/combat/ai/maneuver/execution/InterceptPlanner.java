package com.beyondsignal.game.combat.ai.maneuver.execution;

import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import com.beyondsignal.game.combat.ai.maneuver.geometry.InterceptionCalculator;
import com.beyondsignal.game.combat.ai.maneuver.geometry.InterceptionSolution;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class InterceptPlanner {
    private final InterceptionCalculator interceptionCalculator;

    public InterceptPlanner() {
        this(new InterceptionCalculator());
    }

    public InterceptPlanner(InterceptionCalculator interceptionCalculator) {
        this.interceptionCalculator = Objects.requireNonNull(
            interceptionCalculator,
            "interceptionCalculator"
        );
    }

    public PursuitPlan plan(PursuitContext context) {
        Objects.requireNonNull(context, "context");

        InterceptionSolution solution = interceptionCalculator.calculate(
            context.pursuerPosition(),
            context.maximumSpeed(),
            context.targetPosition(),
            context.targetVelocity()
        );

        double currentDistance = context.currentDistance();
        PursuitState state = selectState(context, solution, currentDistance);

        CombatVector pursuitVector = solution.feasible()
            ? solution.interceptVector()
            : context.targetPosition()
                .subtract(context.pursuerPosition())
                .normalize();

        double desiredClosingSpeed = Math.max(
            0.0,
            Math.min(
                context.maximumSpeed(),
                (currentDistance - context.desiredRange())
                    / Math.max(solution.timeToIntercept(), 1.0)
            )
        );

        Map<String, Integer> reasons = new LinkedHashMap<>();
        reasons.put("interceptFeasible", solution.feasible() ? 45 : 0);
        reasons.put(
            "rangeClosure",
            currentDistance > context.desiredRange() ? 30 : 10
        );
        reasons.put(
            "speedAdvantage",
            context.maximumSpeed() > context.targetVelocity().magnitude()
                ? 25
                : 5
        );

        int confidence = Math.min(
            100,
            reasons.values().stream().mapToInt(Integer::intValue).sum()
        );

        return new PursuitPlan(
            context.pursuerId(),
            context.targetId(),
            state,
            solution.interceptPoint(),
            pursuitVector,
            solution.timeToIntercept(),
            desiredClosingSpeed,
            confidence,
            reasons
        );
    }

    private static PursuitState selectState(
        PursuitContext context,
        InterceptionSolution solution,
        double currentDistance
    ) {
        if (!solution.feasible()) {
            return PursuitState.CLOSING;
        }
        if (currentDistance <= context.desiredRange()) {
            return PursuitState.MATCHING_VELOCITY;
        }
        return PursuitState.INTERCEPTING;
    }
}
