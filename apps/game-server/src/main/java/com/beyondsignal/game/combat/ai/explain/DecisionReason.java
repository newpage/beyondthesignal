package com.beyondsignal.game.combat.ai.explain;

import java.util.Objects;

public record DecisionReason(
    String code,
    String message,
    int weight
) {
    public DecisionReason {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code is required");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message is required");
        }
    }

    public static DecisionReason of(String code, String message, int weight) {
        return new DecisionReason(code, message, weight);
    }
}
