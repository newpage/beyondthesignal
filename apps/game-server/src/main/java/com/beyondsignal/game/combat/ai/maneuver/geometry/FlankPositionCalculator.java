package com.beyondsignal.game.combat.ai.maneuver.geometry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class FlankPositionCalculator {
    public FlankSolution calculate(
        FlankGeometryContext context,
        FlankSide side
    ) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(side, "side");

        CombatVector lateral = context.targetRight().normalize().scale(
            side == FlankSide.RIGHT ? 1.0 : -1.0
        );
        CombatVector destination = context.targetPosition()
            .add(lateral.scale(context.desiredRange()));

        CombatVector approach = destination
            .subtract(context.attackerPosition())
            .normalize();

        double travelDistance = context.attackerPosition().distanceTo(destination);
        double currentDistance = context.attackerPosition()
            .distanceTo(context.targetPosition());

        Map<String, Integer> factors = new LinkedHashMap<>();
        factors.put(
            "travelEfficiency",
            (int) Math.round(
                Math.max(
                    0.0,
                    40.0 - travelDistance / Math.max(context.desiredRange(), 1.0) * 10.0
                )
            )
        );
        factors.put(
            "rangePreservation",
            (int) Math.round(
                Math.max(
                    0.0,
                    25.0 - Math.abs(currentDistance - context.desiredRange())
                        / context.desiredRange() * 25.0
                )
            )
        );
        factors.put("flankArc", 35);

        int score = factors.values().stream().mapToInt(Integer::intValue).sum();
        return new FlankSolution(
            side,
            destination,
            approach,
            travelDistance,
            score,
            factors
        );
    }
}
