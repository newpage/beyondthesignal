package com.beyondsignal.game.combat.ai.maneuver.execution;

import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class OrbitPlanner {
    private final TangentialVelocityCalculator velocityCalculator;

    public OrbitPlanner() {
        this(new TangentialVelocityCalculator());
    }

    public OrbitPlanner(TangentialVelocityCalculator velocityCalculator) {
        this.velocityCalculator = Objects.requireNonNull(
            velocityCalculator,
            "velocityCalculator"
        );
    }

    public OrbitPlan plan(
        OrbitContext context,
        OrbitDirection direction
    ) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(direction, "direction");

        CombatVector radial = context.participantPosition()
            .subtract(context.targetPosition());
        double currentRadius = radial.magnitude();

        CombatVector safeRadial = currentRadius == 0.0
            ? new CombatVector(1.0, 0.0, 0.0)
            : radial.normalize();

        double radiusError = currentRadius - context.desiredRadius();
        OrbitState state = state(context, currentRadius, radiusError);

        CombatVector desiredPosition = context.targetPosition()
            .add(safeRadial.scale(context.desiredRadius()));

        CombatVector tangentialVelocity = velocityCalculator.calculate(
            safeRadial,
            context.orbitNormal(),
            direction,
            context.desiredTangentialSpeed()
        );

        CombatVector radialCorrection = safeRadial.scale(
            -clamp(radiusError, -context.desiredTangentialSpeed(),
                context.desiredTangentialSpeed())
        );

        CombatVector desiredVelocity = tangentialVelocity.add(radialCorrection);

        Map<String, Integer> reasons = new LinkedHashMap<>();
        reasons.put(
            "radiusControl",
            Math.max(0, 45 - (int) Math.round(
                Math.abs(radiusError)
                    / Math.max(context.desiredRadius(), 1.0)
                    * 45.0
            ))
        );
        reasons.put("tangentialMotion", 35);
        reasons.put(
            "orbitEstablished",
            state == OrbitState.ORBITING ? 20 : 5
        );

        int confidence = Math.min(
            100,
            reasons.values().stream().mapToInt(Integer::intValue).sum()
        );

        return new OrbitPlan(
            context.participantId(),
            context.targetId(),
            state,
            direction,
            desiredPosition,
            desiredVelocity,
            radiusError,
            confidence,
            reasons
        );
    }

    private static OrbitState state(
        OrbitContext context,
        double currentRadius,
        double radiusError
    ) {
        if (currentRadius > context.desiredRadius()
            + context.radialTolerance()) {
            return OrbitState.APPROACHING;
        }
        if (currentRadius < context.desiredRadius()
            - context.radialTolerance()) {
            return OrbitState.CORRECTING_RANGE;
        }
        if (context.participantVelocity().magnitude()
            < context.desiredTangentialSpeed() * 0.5) {
            return OrbitState.ESTABLISHING;
        }
        return OrbitState.ORBITING;
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }
}
