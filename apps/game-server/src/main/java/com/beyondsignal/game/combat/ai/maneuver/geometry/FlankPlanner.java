package com.beyondsignal.game.combat.ai.maneuver.geometry;

import java.util.List;
import java.util.Objects;

public final class FlankPlanner {
    private final FlankPositionCalculator calculator;

    public FlankPlanner() {
        this(new FlankPositionCalculator());
    }

    public FlankPlanner(FlankPositionCalculator calculator) {
        this.calculator = Objects.requireNonNull(calculator, "calculator");
    }

    public FlankSolution plan(FlankGeometryContext context) {
        return List.of(
                calculator.calculate(context, FlankSide.LEFT),
                calculator.calculate(context, FlankSide.RIGHT)
            )
            .stream()
            .sorted()
            .findFirst()
            .orElseThrow();
    }

    public List<FlankSolution> candidates(FlankGeometryContext context) {
        return List.of(
                calculator.calculate(context, FlankSide.LEFT),
                calculator.calculate(context, FlankSide.RIGHT)
            )
            .stream()
            .sorted()
            .toList();
    }
}
