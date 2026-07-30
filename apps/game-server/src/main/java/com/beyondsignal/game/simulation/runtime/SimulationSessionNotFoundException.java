package com.beyondsignal.game.simulation.runtime;

import java.util.UUID;

public final class SimulationSessionNotFoundException extends RuntimeException {
    public SimulationSessionNotFoundException(UUID sessionId) {
        super("No active simulation exists for session " + sessionId);
    }
}
