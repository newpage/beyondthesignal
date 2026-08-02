package com.beyondsignal.game.presentation.frame;

import java.util.Objects;
import java.util.UUID;

public record BattleProjectileView(
    UUID id,
    UUID sourceId,
    UUID targetId,
    String weaponId,
    String status,
    double progress
) {
    public BattleProjectileView {
        id = Objects.requireNonNull(id, "id");
        sourceId = Objects.requireNonNull(sourceId, "sourceId");
        targetId = Objects.requireNonNull(targetId, "targetId");
        if (weaponId == null || weaponId.isBlank()) {
            throw new IllegalArgumentException("weaponId is required");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("status is required");
        }
        if (!Double.isFinite(progress) || progress < 0.0 || progress > 1.0) {
            throw new IllegalArgumentException("progress must be between 0 and 1");
        }
    }
}
