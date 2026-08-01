package com.beyondsignal.game.combat.ai.maneuver.geometry;

import com.beyondsignal.game.combat.ai.maneuver.ManeuverType;
import java.util.Objects;

public record GeometryAwareManeuverRecommendation(
    ManeuverType maneuver,
    CombatVector destination,
    CombatVector approachVector,
    int geometryScore,
    String rationale
) {
    public GeometryAwareManeuverRecommendation {
        maneuver = Objects.requireNonNull(maneuver, "maneuver");
        destination = Objects.requireNonNull(destination, "destination");
        approachVector = Objects.requireNonNull(
            approachVector,
            "approachVector"
        );
        if (rationale == null || rationale.isBlank()) {
            throw new IllegalArgumentException("rationale is required");
        }
    }
}
