package com.beyondsignal.game.presentation.frame;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record BattleFrameMetadata(
    UUID battleId,
    long tick,
    double simulationTimeSeconds,
    long seed,
    String frameVersion,
    Instant generatedAt,
    String checksum
) {
    public BattleFrameMetadata {
        battleId = Objects.requireNonNull(battleId, "battleId");
        if (tick < 0) throw new IllegalArgumentException("tick cannot be negative");
        if (!Double.isFinite(simulationTimeSeconds) || simulationTimeSeconds < 0.0) {
            throw new IllegalArgumentException("simulationTimeSeconds must be non-negative");
        }
        if (frameVersion == null || frameVersion.isBlank()) {
            throw new IllegalArgumentException("frameVersion is required");
        }
        generatedAt = Objects.requireNonNull(generatedAt, "generatedAt");
        checksum = Objects.requireNonNull(checksum, "checksum");
    }

    public BattleFrameMetadata withChecksum(String value) {
        return new BattleFrameMetadata(
            battleId, tick, simulationTimeSeconds, seed,
            frameVersion, generatedAt, value
        );
    }
}
