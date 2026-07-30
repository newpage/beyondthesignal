package com.beyondsignal.game.simulation;

import java.util.Objects;
import java.util.UUID;

public record SelectTargetCommand(UUID targetId) implements ShipCommand {
    public SelectTargetCommand {
        Objects.requireNonNull(targetId, "targetId must not be null");
    }
}
