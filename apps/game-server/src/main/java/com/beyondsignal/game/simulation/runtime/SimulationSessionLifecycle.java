package com.beyondsignal.game.simulation.runtime;

import java.util.UUID;

public interface SimulationSessionLifecycle {
    void start(UUID sessionId);
    boolean stop(UUID sessionId);
}
