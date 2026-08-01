package com.beyondsignal.game.combat.ai.maneuver.geometry;

import com.beyondsignal.game.combat.ai.maneuver.ManeuverType;
import java.util.Objects;

public final class GeometryAwareManeuverPlanner {
    private final FlankPlanner flankPlanner;
    private final ApproachVectorEvaluator approachEvaluator;

    public GeometryAwareManeuverPlanner() {
        this(new FlankPlanner(), new ApproachVectorEvaluator());
    }

    public GeometryAwareManeuverPlanner(
        FlankPlanner flankPlanner,
        ApproachVectorEvaluator approachEvaluator
    ) {
        this.flankPlanner = Objects.requireNonNull(
            flankPlanner,
            "flankPlanner"
        );
        this.approachEvaluator = Objects.requireNonNull(
            approachEvaluator,
            "approachEvaluator"
        );
    }

    public GeometryAwareManeuverRecommendation recommendFlank(
        FlankGeometryContext context,
        double friendlySupport,
        double formationIntegrity
    ) {
        FlankSolution solution = flankPlanner.plan(context);

        ApproachVectorScore approach = approachEvaluator.evaluate(
            solution.approachVector(),
            context.targetForward(),
            context.targetRight(),
            friendlySupport,
            formationIntegrity
        );

        ManeuverType maneuver = solution.side() == FlankSide.LEFT
            ? ManeuverType.FLANK_LEFT
            : ManeuverType.FLANK_RIGHT;

        return new GeometryAwareManeuverRecommendation(
            maneuver,
            solution.destination(),
            solution.approachVector(),
            solution.score() + approach.score(),
            "Selected "
                + solution.side().name().toLowerCase()
                + " flank based on travel efficiency and attack-arc advantage."
        );
    }
}
