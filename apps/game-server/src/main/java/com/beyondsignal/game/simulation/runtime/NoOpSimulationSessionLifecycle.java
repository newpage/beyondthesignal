package com.beyondsignal.game.simulation.runtime;

import java.util.UUID;

public enum NoOpSimulationSessionLifecycle implements SimulationSessionLifecycle {
    INSTANCE;

    @Override
    public void start(UUID sessionId) {
        // Used by service-only tests and deployments without simulation enabled.
    }

    @Override
    public boolean stop(UUID sessionId) {
        return false;
    }
}
