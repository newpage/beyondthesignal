package com.beyondsignal.game.presentation.context;

import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import java.time.Instant;
import java.util.Objects;

public record PresentationContext(
    CombatAiSnapshot snapshot,
    long seed,
    long frameSequence,
    Instant generatedAt,
    PresentationConfiguration configuration,
    PresentationDebugOptions debugOptions
) {
    public PresentationContext {
        snapshot = Objects.requireNonNull(snapshot, "snapshot");
        if (frameSequence < 0) {
            throw new IllegalArgumentException("frameSequence cannot be negative");
        }
        generatedAt = Objects.requireNonNull(generatedAt, "generatedAt");
        configuration = Objects.requireNonNull(configuration, "configuration");
        debugOptions = Objects.requireNonNull(debugOptions, "debugOptions");
    }

    public double simulationTimeSeconds() {
        return snapshot.tick() * configuration.secondsPerTick();
    }
}
