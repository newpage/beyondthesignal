package com.beyondsignal.game.combat.ai.maneuver.execution;

import java.util.Objects;

public final class KitingController {
    private final KitingPlanner planner;
    private KitingPlan activePlan;

    public KitingController() {
        this(new KitingPlanner());
    }

    public KitingController(KitingPlanner planner) {
        this.planner = Objects.requireNonNull(planner, "planner");
    }

    public synchronized KitingPlan update(KitingContext context) {
        KitingPlan candidate = planner.plan(context);

        if (activePlan == null || shouldReplace(context, activePlan, candidate)) {
            activePlan = candidate;
        }

        return activePlan;
    }

    public synchronized KitingPlan forceReplan(KitingContext context) {
        activePlan = planner.plan(context);
        return activePlan;
    }

    private static boolean shouldReplace(
        KitingContext context,
        KitingPlan active,
        KitingPlan candidate
    ) {
        double velocityShift = active.desiredVelocity()
            .distanceTo(candidate.desiredVelocity());

        return active.state() != candidate.state()
            || velocityShift > context.maximumSpeed() * 0.20;
    }
}
