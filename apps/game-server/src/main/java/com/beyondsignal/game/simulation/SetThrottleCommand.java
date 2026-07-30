package com.beyondsignal.game.simulation;

public record SetThrottleCommand(int throttle) implements ShipCommand {
    public SetThrottleCommand {
        if (throttle < 0 || throttle > 100) {
            throw new IllegalArgumentException("throttle must be between 0 and 100");
        }
    }
}
