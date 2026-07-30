package com.beyondsignal.game.simulation;

import java.util.Objects;

public record AllocatePowerCommand(ShipSubsystem subsystem, int powerAllocation) implements ShipCommand {
    public AllocatePowerCommand {
        Objects.requireNonNull(subsystem, "subsystem must not be null");
        if (powerAllocation < 0 || powerAllocation > 100) {
            throw new IllegalArgumentException("powerAllocation must be between 0 and 100");
        }
    }
}
