package com.beyondsignal.game.combat.ai.maneuver.execution;

import java.util.Objects;

public final class PursuitController {
    private final InterceptPlanner planner;
    private PursuitPlan activePlan;

    public PursuitController() {
        this(new InterceptPlanner());
    }

    public PursuitController(InterceptPlanner planner) {
        this.planner = Objects.requireNonNull(planner, "planner");
    }

    public synchronized PursuitPlan update(PursuitContext context) {
        Objects.requireNonNull(context, "context");

        PursuitPlan candidate = planner.plan(context);

        if (activePlan == null || shouldReplan(context, activePlan, candidate)) {
            activePlan = candidate;
        }

        return activePlan;
    }

    public synchronized PursuitPlan forceReplan(PursuitContext context) {
        activePlan = planner.plan(context);
        return activePlan;
    }

    public synchronized boolean hasActivePlan() {
        return activePlan != null;
    }

    private static boolean shouldReplan(
        PursuitContext context,
        PursuitPlan activePlan,
        PursuitPlan candidate
    ) {
        double interceptShift = activePlan.interceptPoint()
            .distanceTo(candidate.interceptPoint());

        double threshold = Math.max(
            context.desiredRange() * 0.25,
            25.0
        );

        return interceptShift > threshold
            || activePlan.state() != candidate.state()
            || activePlan.state() == PursuitState.ABORTED
            || activePlan.state() == PursuitState.COMPLETED;
    }
}
