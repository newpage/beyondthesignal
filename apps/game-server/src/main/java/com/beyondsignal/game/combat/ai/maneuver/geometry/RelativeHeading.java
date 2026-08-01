package com.beyondsignal.game.combat.ai.maneuver.geometry;

public record RelativeHeading(
    double cosine,
    double angleDegrees,
    AttackArc arc
) {
    public RelativeHeading {
        if (!Double.isFinite(cosine) || cosine < -1.0 || cosine > 1.0) {
            throw new IllegalArgumentException("cosine must be between -1 and 1");
        }
        if (!Double.isFinite(angleDegrees)
            || angleDegrees < 0.0
            || angleDegrees > 180.0) {
            throw new IllegalArgumentException(
                "angleDegrees must be between 0 and 180"
            );
        }
        if (arc == null) {
            throw new IllegalArgumentException("arc is required");
        }
    }
}
