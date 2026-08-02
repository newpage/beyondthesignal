package com.beyondsignal.game.presentation.frame;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record BattleMovementView(
    UUID shipId,
    PresentationVector desiredVelocity,
    PresentationVector interceptPoint,
    List<PresentationVector> plannedPath
) {
    public BattleMovementView {
        shipId = Objects.requireNonNull(shipId, "shipId");
        desiredVelocity = Objects.requireNonNull(desiredVelocity, "desiredVelocity");
        interceptPoint = Objects.requireNonNull(interceptPoint, "interceptPoint");
        plannedPath = List.copyOf(plannedPath);
    }
}
