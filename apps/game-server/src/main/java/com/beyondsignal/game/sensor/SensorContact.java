package com.beyondsignal.game.sensor;

import java.util.Objects;

public record SensorContact(
    String id,
    String displayName,
    SensorContactType type,
    double distanceLightYears,
    double signalStrength,
    boolean hostile,
    boolean identified
) {
    public SensorContact {
        id = requireText(id, "id");
        displayName = requireText(displayName, "displayName");
        type = Objects.requireNonNull(type, "type");
        if (!Double.isFinite(distanceLightYears) || distanceLightYears < 0.0) {
            throw new IllegalArgumentException("distanceLightYears must be finite and non-negative");
        }
        if (!Double.isFinite(signalStrength) || signalStrength < 0.0 || signalStrength > 1.0) {
            throw new IllegalArgumentException("signalStrength must be between 0.0 and 1.0");
        }
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value;
    }
}
