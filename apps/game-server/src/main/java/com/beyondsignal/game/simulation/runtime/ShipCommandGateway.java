package com.beyondsignal.game.simulation.runtime;

import com.beyondsignal.game.simulation.ShipCommand;

import java.util.UUID;

@FunctionalInterface
public interface ShipCommandGateway {
    void submit(UUID sessionId, ShipCommand command);
}
