package com.beyondsignal.game.presentation.frame;

import java.util.Objects;
import java.util.UUID;

public record BattleWreckView(
    UUID id,
    UUID formerShipId,
    String faction,
    long destroyedTick,
    String cause
) {
    public BattleWreckView {
        id = Objects.requireNonNull(id, "id");
        formerShipId = Objects.requireNonNull(formerShipId, "formerShipId");
        faction = Objects.requireNonNull(faction, "faction");
        cause = Objects.requireNonNull(cause, "cause");
    }
}
