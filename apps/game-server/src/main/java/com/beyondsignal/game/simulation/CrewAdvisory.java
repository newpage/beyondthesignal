package com.beyondsignal.game.simulation;

import java.util.Objects;

public record CrewAdvisory(long sequence, String station, String severity, String message) {
    public CrewAdvisory {
        if (sequence < 0) throw new IllegalArgumentException("sequence must not be negative");
        station = requireText(station, "station");
        severity = requireText(severity, "severity");
        message = requireText(message, "message");
    }

    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name + " must not be null");
        String result = value.trim();
        if (result.isEmpty()) throw new IllegalArgumentException(name + " must not be blank");
        return result;
    }
}
