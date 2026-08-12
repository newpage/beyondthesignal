package com.beyondsignal.game.presentation.frame;

import java.util.Objects;
import java.util.UUID;

public record BattleThreatView(
    UUID participantId,
    double score,
    int firepower,
    double hullRatio
) {
    public BattleThreatView {
        participantId = Objects.requireNonNull(participantId, "participantId");
        if (!Double.isFinite(score) || score < 0.0) {
            throw new IllegalArgumentException(
                "score must be finite and non-negative"
            );
        }
    }
}
