package com.beyondsignal.game.simulation.runtime;

import com.beyondsignal.game.simulation.ShipState;

@FunctionalInterface
public interface SimulationStateListener {
    void onStateAdvanced(ShipState state);
}
