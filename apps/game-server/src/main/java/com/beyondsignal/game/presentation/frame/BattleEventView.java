package com.beyondsignal.game.presentation.frame;

import java.util.Map;
import java.util.Objects;

public record BattleEventView(
    long tick,
    String type,
    String message,
    Map<String, String> attributes
) {
    public BattleEventView {
        if (tick < 0) throw new IllegalArgumentException("tick cannot be negative");
        type = Objects.requireNonNull(type, "type");
        message = Objects.requireNonNull(message, "message");
        attributes = Map.copyOf(attributes);
    }
}
