package com.beyondsignal.game.sensor;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record SensorScanResult(
    String originSystemId,
    double rangeLightYears,
    Instant completedAt,
    List<SensorContact> contacts
) {
    public SensorScanResult {
        if (originSystemId == null || originSystemId.isBlank()) {
            throw new IllegalArgumentException("originSystemId is required");
        }
        if (!Double.isFinite(rangeLightYears) || rangeLightYears <= 0.0) {
            throw new IllegalArgumentException("rangeLightYears must be positive");
        }
        completedAt = Objects.requireNonNull(completedAt, "completedAt");
        contacts = List.copyOf(Objects.requireNonNull(contacts, "contacts"));
    }

    public long identifiedCount() {
        return contacts.stream().filter(SensorContact::identified).count();
    }

    public long hostileCount() {
        return contacts.stream().filter(SensorContact::hostile).count();
    }
}
