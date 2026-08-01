package com.beyondsignal.game.combat.ai.maneuver.execution;

import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class KitingPlanner {
    private final RangeKeeper rangeKeeper;

    public KitingPlanner() {
        this(new RangeKeeper());
    }

    public KitingPlanner(RangeKeeper rangeKeeper) {
        this.rangeKeeper = Objects.requireNonNull(rangeKeeper, "rangeKeeper");
    }

    public KitingPlan plan(KitingContext context) {
        Objects.requireNonNull(context, "context");

        RangeControlDecision range = rangeKeeper.control(
            context.currentRange(),
            context.desiredRange(),
            context.rangeTolerance(),
            context.maximumSpeed()
        );

        CombatVector awayFromTarget = context.participantPosition()
            .subtract(context.targetPosition());

        CombatVector safeAway = awayFromTarget.magnitude() == 0.0
            ? new CombatVector(1.0, 0.0, 0.0)
            : awayFromTarget.normalize();

        CombatVector desiredVelocity = switch (range.state()) {
            case TOO_CLOSE -> safeAway.scale(Math.abs(range.speedCommand()));
            case TOO_FAR -> safeAway.scale(-Math.abs(range.speedCommand()));
            case IN_BAND -> context.targetVelocity();
        };

        Map<String, Integer> reasons = new LinkedHashMap<>();
        reasons.put(
            "rangeControl",
            range.state() == RangeControlState.IN_BAND ? 45 : 35
        );
        reasons.put("targetTracking", 30);
        reasons.put(
            "distanceSafety",
            range.state() == RangeControlState.TOO_CLOSE ? 25 : 15
        );

        int confidence = Math.min(
            100,
            reasons.values().stream().mapToInt(Integer::intValue).sum()
        );

        return new KitingPlan(
            context.participantId(),
            context.targetId(),
            range.state(),
            desiredVelocity,
            context.currentRange(),
            context.desiredRange(),
            confidence,
            reasons
        );
    }
}
