package com.beyondsignal.game.combat.ai.maneuver.geometry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class ApproachVectorEvaluator {
    public ApproachVectorScore evaluate(
        CombatVector approach,
        CombatVector targetForward,
        CombatVector targetRight,
        double friendlySupport,
        double formationIntegrity
    ) {
        Objects.requireNonNull(approach, "approach");
        Objects.requireNonNull(targetForward, "targetForward");
        Objects.requireNonNull(targetRight, "targetRight");
        validateUnit(friendlySupport, "friendlySupport");
        validateUnit(formationIntegrity, "formationIntegrity");

        CombatVector normalizedApproach = approach.normalize();
        RelativeHeading heading = new RelativeHeadingAnalyzer().analyze(
            targetForward,
            normalizedApproach.scale(-1.0),
            targetRight
        );

        Map<String, Integer> factors = new LinkedHashMap<>();
        factors.put("arcAdvantage", switch (heading.arc()) {
            case REAR -> 40;
            case LEFT_FLANK, RIGHT_FLANK -> 30;
            case FORWARD -> 5;
        });
        factors.put("friendlySupport", (int) Math.round(friendlySupport * 20.0));
        factors.put(
            "formationCompatibility",
            (int) Math.round(formationIntegrity * 20.0)
        );

        int score = factors.values().stream().mapToInt(Integer::intValue).sum();
        return new ApproachVectorScore(
            normalizedApproach,
            score,
            factors
        );
    }

    private static void validateUnit(double value, String name) {
        if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
    }
}
