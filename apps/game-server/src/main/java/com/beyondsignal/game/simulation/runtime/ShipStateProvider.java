package com.beyondsignal.game.simulation.runtime;

import com.beyondsignal.game.simulation.ShipState;

import java.util.Optional;
import java.util.UUID;

@FunctionalInterface
public interface ShipStateProvider {
    Optional<ShipState> findState(UUID sessionId);
}
