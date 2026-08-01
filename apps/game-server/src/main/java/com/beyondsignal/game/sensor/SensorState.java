package com.beyondsignal.game.sensor;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

public record SensorState(
    Duration cooldownRemaining,
    long scansCompleted,
    SensorScanResult lastScan
) {
    public SensorState {
        cooldownRemaining = Objects.requireNonNull(cooldownRemaining, "cooldownRemaining");
        if (cooldownRemaining.isNegative()) {
            throw new IllegalArgumentException("cooldownRemaining cannot be negative");
        }
        if (scansCompleted < 0) {
            throw new IllegalArgumentException("scansCompleted cannot be negative");
        }
    }

    public static SensorState ready() {
        return new SensorState(Duration.ZERO, 0L, null);
    }

    public boolean readyForScan() {
        return cooldownRemaining.isZero();
    }

    public Optional<SensorScanResult> lastScanOptional() {
        return Optional.ofNullable(lastScan);
    }

    public SensorState tick(Duration delta) {
        if (delta == null || delta.isNegative()) {
            throw new IllegalArgumentException("delta must be non-negative");
        }
        Duration remaining = cooldownRemaining.minus(delta);
        if (remaining.isNegative()) {
            remaining = Duration.ZERO;
        }
        return new SensorState(remaining, scansCompleted, lastScan);
    }

    public SensorState recordScan(SensorScanResult result, Duration cooldown) {
        Objects.requireNonNull(result, "result");
        Objects.requireNonNull(cooldown, "cooldown");
        if (cooldown.isNegative() || cooldown.isZero()) {
            throw new IllegalArgumentException("cooldown must be positive");
        }
        return new SensorState(cooldown, scansCompleted + 1, result);
    }
}
