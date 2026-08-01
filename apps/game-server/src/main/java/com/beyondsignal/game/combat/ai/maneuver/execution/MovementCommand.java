package com.beyondsignal.game.combat.ai.maneuver.execution;

import com.beyondsignal.game.combat.ai.maneuver.geometry.CombatVector;
import java.util.Objects;

public record MovementCommand(
    CombatVector desiredHeading,
    double accelerationCommand,
    double speedCommand
) {
    public MovementCommand {
        desiredHeading = Objects.requireNonNull(desiredHeading, "desiredHeading");
        if (desiredHeading.magnitude() == 0.0) {
            throw new IllegalArgumentException("desiredHeading cannot be zero");
        }
        if (!Double.isFinite(accelerationCommand)) {
            throw new IllegalArgumentException("accelerationCommand must be finite");
        }
        if (!Double.isFinite(speedCommand) || speedCommand < 0.0) {
            throw new IllegalArgumentException("speedCommand must be non-negative");
        }
    }
}
