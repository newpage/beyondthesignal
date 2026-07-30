package com.beyondsignal.game.simulation.runtime;

import java.util.Objects;
import java.util.UUID;

public final class RuntimeSimulationSessionLifecycle implements SimulationSessionLifecycle {
    private final SimulationRuntime runtime;

    public RuntimeSimulationSessionLifecycle(SimulationRuntime runtime) {
        this.runtime = Objects.requireNonNull(runtime, "runtime must not be null");
    }

    @Override
    public void start(UUID sessionId) {
        runtime.start(sessionId);
    }

    @Override
    public boolean stop(UUID sessionId) {
        return runtime.stop(sessionId);
    }
}
