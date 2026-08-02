package com.beyondsignal.game.presentation.integration;

import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.CombatSnapshotFactory;
import com.beyondsignal.game.combat.engine.CombatEncounter;
import com.beyondsignal.game.combat.engine.CombatTickResult;
import com.beyondsignal.game.presentation.context.PresentationConfiguration;
import com.beyondsignal.game.presentation.context.PresentationContext;
import com.beyondsignal.game.presentation.context.PresentationDebugOptions;
import com.beyondsignal.game.presentation.frame.BattleFrameV1;
import com.beyondsignal.game.presentation.pipeline.PresentationPipeline;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public final class CombatPresentationCoordinator
    implements CombatTickPresentationHook {

    private final CombatSnapshotFactory snapshotFactory;
    private final PresentationPipeline pipeline;
    private final PresentationConfiguration configuration;
    private final PresentationDebugOptions debugOptions;
    private final AtomicLong sequence = new AtomicLong();

    public CombatPresentationCoordinator(
        CombatSnapshotFactory snapshotFactory,
        PresentationPipeline pipeline,
        PresentationConfiguration configuration,
        PresentationDebugOptions debugOptions
    ) {
        this.snapshotFactory = Objects.requireNonNull(
            snapshotFactory,
            "snapshotFactory"
        );
        this.pipeline = Objects.requireNonNull(pipeline, "pipeline");
        this.configuration = Objects.requireNonNull(
            configuration,
            "configuration"
        );
        this.debugOptions = Objects.requireNonNull(
            debugOptions,
            "debugOptions"
        );
    }

    @Override
    public void onTick(
        CombatEncounter encounter,
        CombatTickResult tickResult
    ) {
        publish(encounter, tickResult.eventsProduced());
    }

    public BattleFrameV1 publish(CombatEncounter encounter) {
        return publish(encounter, java.util.List.of());
    }

    private BattleFrameV1 publish(
        CombatEncounter encounter,
        java.util.List<com.beyondsignal.game.combat.event.CombatEvent> events
    ) {
        CombatAiSnapshot snapshot = snapshotFactory.create(encounter);
        long frameSequence = sequence.getAndIncrement();

        return pipeline.publish(new PresentationContext(
            snapshot,
            encounter.context().seed(),
            frameSequence,
            deterministicTimestamp(snapshot.tick()),
            configuration,
            debugOptions,
            events,
            encounter.projectiles(),
            encounter.wrecks(),
            encounter.fleets()
        ));
    }

    private Instant deterministicTimestamp(long tick) {
        long milliseconds = Math.round(
            tick * configuration.secondsPerTick() * 1_000.0
        );
        return Instant.EPOCH.plusMillis(milliseconds);
    }
}
