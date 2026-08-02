package com.beyondsignal.game.presentation.context;

import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.event.CombatEvent;
import com.beyondsignal.game.combat.projectile.ProjectileState;
import java.util.List;
import java.time.Instant;
import java.util.Objects;

public record PresentationContext(
    CombatAiSnapshot snapshot,
    long seed,
    long frameSequence,
    Instant generatedAt,
    PresentationConfiguration configuration,
    PresentationDebugOptions debugOptions,
    List<CombatEvent> combatEvents,
    List<ProjectileState> projectiles
) {
    public PresentationContext {
        snapshot = Objects.requireNonNull(snapshot, "snapshot");
        if (frameSequence < 0) {
            throw new IllegalArgumentException("frameSequence cannot be negative");
        }
        generatedAt = Objects.requireNonNull(generatedAt, "generatedAt");
        configuration = Objects.requireNonNull(configuration, "configuration");
        debugOptions = Objects.requireNonNull(debugOptions, "debugOptions");
        combatEvents = List.copyOf(
            Objects.requireNonNull(combatEvents, "combatEvents")
        );
        projectiles = List.copyOf(
            Objects.requireNonNull(projectiles, "projectiles")
        );
    }

    public PresentationContext(
        CombatAiSnapshot snapshot,
        long seed,
        long frameSequence,
        Instant generatedAt,
        PresentationConfiguration configuration,
        PresentationDebugOptions debugOptions
    ) {
        this(
            snapshot,
            seed,
            frameSequence,
            generatedAt,
            configuration,
            debugOptions,
            List.of(),
            List.of()
        );
    }

    public PresentationContext(
        CombatAiSnapshot snapshot,
        long seed,
        long frameSequence,
        Instant generatedAt,
        PresentationConfiguration configuration,
        PresentationDebugOptions debugOptions,
        List<CombatEvent> combatEvents
    ) {
        this(
            snapshot,
            seed,
            frameSequence,
            generatedAt,
            configuration,
            debugOptions,
            combatEvents,
            List.of()
        );
    }

    public double simulationTimeSeconds() {
        return snapshot.tick() * configuration.secondsPerTick();
    }
}
