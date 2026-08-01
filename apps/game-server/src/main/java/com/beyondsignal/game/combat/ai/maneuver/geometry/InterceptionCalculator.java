package com.beyondsignal.game.combat.ai.maneuver.geometry;

import java.util.Objects;

public final class InterceptionCalculator {
    public InterceptionSolution calculate(
        CombatVector interceptorPosition,
        double interceptorSpeed,
        CombatVector targetPosition,
        CombatVector targetVelocity
    ) {
        Objects.requireNonNull(interceptorPosition, "interceptorPosition");
        Objects.requireNonNull(targetPosition, "targetPosition");
        Objects.requireNonNull(targetVelocity, "targetVelocity");

        if (!Double.isFinite(interceptorSpeed) || interceptorSpeed <= 0.0) {
            throw new IllegalArgumentException("interceptorSpeed must be positive");
        }

        CombatVector relative = targetPosition.subtract(interceptorPosition);

        double a = targetVelocity.dot(targetVelocity)
            - interceptorSpeed * interceptorSpeed;
        double b = 2.0 * relative.dot(targetVelocity);
        double c = relative.dot(relative);

        double time = solvePositiveTime(a, b, c);
        if (!Double.isFinite(time)) {
            return new InterceptionSolution(
                targetPosition,
                0.0,
                targetPosition.subtract(interceptorPosition).normalize(),
                false
            );
        }

        CombatVector interceptPoint = targetPosition.add(
            targetVelocity.scale(time)
        );
        CombatVector interceptVector = interceptPoint
            .subtract(interceptorPosition)
            .normalize();

        return new InterceptionSolution(
            interceptPoint,
            time,
            interceptVector,
            true
        );
    }

    private static double solvePositiveTime(double a, double b, double c) {
        if (Math.abs(a) < 1.0e-9) {
            if (Math.abs(b) < 1.0e-9) {
                return Double.NaN;
            }
            double time = -c / b;
            return time > 0.0 ? time : Double.NaN;
        }

        double discriminant = b * b - 4.0 * a * c;
        if (discriminant < 0.0) {
            return Double.NaN;
        }

        double root = Math.sqrt(discriminant);
        double first = (-b - root) / (2.0 * a);
        double second = (-b + root) / (2.0 * a);

        double result = Double.POSITIVE_INFINITY;
        if (first > 0.0) {
            result = first;
        }
        if (second > 0.0) {
            result = Math.min(result, second);
        }

        return Double.isFinite(result) ? result : Double.NaN;
    }
}
