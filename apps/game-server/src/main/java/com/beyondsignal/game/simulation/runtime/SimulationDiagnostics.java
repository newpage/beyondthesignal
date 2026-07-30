package com.beyondsignal.game.simulation.runtime;

import com.beyondsignal.game.simulation.ShipState;

import java.util.Objects;

public record SimulationDiagnostics(
    ShipState state,
    int commandQueueDepth,
    double configuredTickRateHz,
    double lastTickDurationMillis,
    double averageTickDurationMillis
) {
    public SimulationDiagnostics {
        Objects.requireNonNull(state, "state must not be null");
        if (commandQueueDepth < 0) {
            throw new IllegalArgumentException("commandQueueDepth must not be negative");
        }
    }
}
