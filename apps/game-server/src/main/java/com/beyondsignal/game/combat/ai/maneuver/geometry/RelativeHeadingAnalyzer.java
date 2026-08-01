package com.beyondsignal.game.combat.ai.maneuver.geometry;

import java.util.Objects;

public final class RelativeHeadingAnalyzer {
    public RelativeHeading analyze(
        CombatVector targetForward,
        CombatVector directionFromTargetToAttacker,
        CombatVector targetRight
    ) {
        Objects.requireNonNull(targetForward, "targetForward");
        Objects.requireNonNull(
            directionFromTargetToAttacker,
            "directionFromTargetToAttacker"
        );
        Objects.requireNonNull(targetRight, "targetRight");

        CombatVector forward = targetForward.normalize();
        CombatVector direction = directionFromTargetToAttacker.normalize();
        CombatVector right = targetRight.normalize();

        double cosine = clamp(forward.dot(direction));
        double angle = Math.toDegrees(Math.acos(cosine));

        AttackArc arc;
        if (angle <= 45.0) {
            arc = AttackArc.FORWARD;
        } else if (angle >= 135.0) {
            arc = AttackArc.REAR;
        } else {
            arc = right.dot(direction) >= 0.0
                ? AttackArc.RIGHT_FLANK
                : AttackArc.LEFT_FLANK;
        }

        return new RelativeHeading(cosine, angle, arc);
    }

    private static double clamp(double value) {
        return Math.max(-1.0, Math.min(1.0, value));
    }
}
