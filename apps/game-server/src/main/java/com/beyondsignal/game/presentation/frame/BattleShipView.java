package com.beyondsignal.game.presentation.frame;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record BattleShipView(
    UUID id,
    String name,
    String shipClass,
    String faction,
    PresentationVector position,
    PresentationVector velocity,
    double headingRadians,
    UUID targetId,
    double hullPercent,
    double shieldPercent,
    String maneuver,
    String executionState,
    int confidence,
    Set<String> renderFlags
) {
    public BattleShipView {
        id = Objects.requireNonNull(id, "id");
        requireText(name, "name");
        requireText(shipClass, "shipClass");
        requireText(faction, "faction");
        position = Objects.requireNonNull(position, "position");
        velocity = Objects.requireNonNull(velocity, "velocity");
        if (!Double.isFinite(headingRadians)) {
            throw new IllegalArgumentException("headingRadians must be finite");
        }
        requireUnit(hullPercent, "hullPercent");
        requireUnit(shieldPercent, "shieldPercent");
        maneuver = Objects.requireNonNullElse(maneuver, "NONE");
        executionState = Objects.requireNonNullElse(executionState, "IDLE");
        if (confidence < 0 || confidence > 100) {
            throw new IllegalArgumentException("confidence must be between 0 and 100");
        }
        renderFlags = Set.copyOf(renderFlags);
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is required");
        }
    }

    private static void requireUnit(double value, String name) {
        if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
    }
}
