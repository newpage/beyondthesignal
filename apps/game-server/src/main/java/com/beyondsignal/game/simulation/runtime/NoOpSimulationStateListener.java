package com.beyondsignal.game.simulation.runtime;

import com.beyondsignal.game.simulation.ShipState;

public enum NoOpSimulationStateListener implements SimulationStateListener {
    INSTANCE;

    @Override
    public void onStateAdvanced(ShipState state) {
        // Intentionally empty.
    }
}
