package com.beyondsignal.game.presentation.context;

import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.event.CombatEvent;
import com.beyondsignal.game.combat.projectile.ProjectileState;
import com.beyondsignal.game.combat.fleet.FleetOrder;
import com.beyondsignal.game.combat.fleet.FleetState;
import com.beyondsignal.game.combat.fleet.SquadronState;
import com.beyondsignal.game.combat.wreck.WreckState;
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
    List<ProjectileState> projectiles,
    List<WreckState> wrecks,
    List<FleetState> fleets
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
        wrecks = List.copyOf(
            Objects.requireNonNull(wrecks, "wrecks")
        );
        fleets = List.copyOf(
            Objects.requireNonNull(fleets, "fleets")
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
            List.of(),
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
            List.of(),
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
    List<CombatEvent> combatEvents,
    List<ProjectileState> projectiles
) {
    this(
        snapshot,
        seed,
        frameSequence,
        generatedAt,
        configuration,
        debugOptions,
        combatEvents,
        projectiles,
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
        List<CombatEvent> combatEvents,
        List<ProjectileState> projectiles,
        List<WreckState> wrecks
    ) {
        this(
            snapshot,
            seed,
            frameSequence,
            generatedAt,
            configuration,
            debugOptions,
            combatEvents,
            projectiles,
            wrecks,
            List.of()
        );
    }

    public List<SquadronState> squadrons() {
        return fleets.stream()
            .flatMap(fleet -> fleet.squadrons().stream())
            .sorted(java.util.Comparator.comparing(
                squadron -> squadron.squadronId().toString()
            ))
            .toList();
    }

    public List<FleetOrder> fleetOrders() {
        return fleets.stream()
            .flatMap(fleet -> fleet.orders().stream())
            .sorted(
                java.util.Comparator.comparingInt(FleetOrder::priority)
                    .thenComparing(order -> order.orderId().toString())
            )
            .toList();
    }

    public double simulationTimeSeconds() {
        return snapshot.tick() * configuration.secondsPerTick();
    }
}
