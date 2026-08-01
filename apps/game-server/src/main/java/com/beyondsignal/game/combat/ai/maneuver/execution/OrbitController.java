package com.beyondsignal.game.combat.ai.maneuver.execution;

import java.util.Objects;

public final class OrbitController {
    private final OrbitPlanner planner;
    private OrbitPlan activePlan;

    public OrbitController() {
        this(new OrbitPlanner());
    }

    public OrbitController(OrbitPlanner planner) {
        this.planner = Objects.requireNonNull(planner, "planner");
    }

    public synchronized OrbitPlan update(
        OrbitContext context,
        OrbitDirection direction
    ) {
        OrbitPlan candidate = planner.plan(context, direction);

        if (activePlan == null || shouldReplace(context, activePlan, candidate)) {
            activePlan = candidate;
        }

        return activePlan;
    }

    public synchronized OrbitPlan forceReplan(
        OrbitContext context,
        OrbitDirection direction
    ) {
        activePlan = planner.plan(context, direction);
        return activePlan;
    }

    private static boolean shouldReplace(
        OrbitContext context,
        OrbitPlan active,
        OrbitPlan candidate
    ) {
        double destinationShift = active.desiredPosition()
            .distanceTo(candidate.desiredPosition());

        double threshold = Math.max(
            context.radialTolerance(),
            context.desiredRadius() * 0.10
        );

        return destinationShift > threshold
            || active.state() != candidate.state()
            || active.direction() != candidate.direction()
            || active.state() == OrbitState.ABORTED;
    }
}
