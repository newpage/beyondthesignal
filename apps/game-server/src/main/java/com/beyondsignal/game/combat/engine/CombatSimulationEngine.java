package com.beyondsignal.game.combat.engine;

import com.beyondsignal.game.combat.command.CombatCommand;
import com.beyondsignal.game.combat.event.CombatEventBatch;
import com.beyondsignal.game.combat.event.CombatEventBus;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.rng.SplitMix64CombatRandom;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import com.beyondsignal.game.presentation.integration.CombatTickPresentationHook;

/**
 * Registry and lifecycle boundary for deterministic combat encounters.
 */
public final class CombatSimulationEngine {
    private final Map<CombatId, CombatEncounter> encounters = new LinkedHashMap<>();
    private final CombatTickProcessor tickProcessor;
    private final CombatTickPresentationHook presentationHook;
    private final CombatEventBus eventBus;

    public CombatSimulationEngine() {
        this(
            new CombatTickProcessor(),
            CombatTickPresentationHook.noOp(),
            new CombatEventBus()
        );
    }

    public CombatSimulationEngine(CombatTickProcessor tickProcessor) {
        this(
            tickProcessor,
            CombatTickPresentationHook.noOp(),
            new CombatEventBus()
        );
    }

    public CombatSimulationEngine(
        CombatTickProcessor tickProcessor,
        CombatTickPresentationHook presentationHook
    ) {
        this(tickProcessor, presentationHook, new CombatEventBus());
    }

    public CombatSimulationEngine(
        CombatTickProcessor tickProcessor,
        CombatTickPresentationHook presentationHook,
        CombatEventBus eventBus
    ) {
        this.tickProcessor = Objects.requireNonNull(
            tickProcessor,
            "tickProcessor"
        );
        this.presentationHook = Objects.requireNonNull(
            presentationHook,
            "presentationHook"
        );
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
    }

    public synchronized CombatEncounter createEncounter(CombatId combatId, long seed) {
        Objects.requireNonNull(combatId, "combatId");
        if (encounters.containsKey(combatId)) {
            throw new IllegalArgumentException("Combat encounter already exists: " + combatId);
        }

        CombatEncounter encounter = new CombatEncounter(
            new CombatContext(
                combatId,
                seed,
                new SplitMix64CombatRandom(seed),
                new CombatClock()
            )
        );
        encounters.put(combatId, encounter);
        return encounter;
    }

    public synchronized Optional<CombatEncounter> encounter(CombatId combatId) {
        return Optional.ofNullable(encounters.get(combatId));
    }

    public synchronized void submit(CombatCommand command) {
        CombatEncounter encounter = encounters.get(command.combatId());
        if (encounter == null) {
            throw new IllegalArgumentException("Unknown combat encounter: " + command.combatId());
        }
        encounter.submit(command);
    }

    public synchronized CombatTickResult tick(CombatId combatId) {
        CombatEncounter encounter = encounters.get(combatId);
        if (encounter == null) {
            throw new IllegalArgumentException("Unknown combat encounter: " + combatId);
        }
        CombatTickResult result = tickProcessor.process(encounter);
        eventBus.publish(CombatEventBatch.from(
            encounter.combatId(),
            result.tick(),
            result.eventsProduced()
        ));
        presentationHook.onTick(encounter, result);
        return result;
    }

    public CombatEventBus eventBus() {
        return eventBus;
    }

    public synchronized boolean completed(CombatId combatId) {
        return encounter(combatId)
            .map(value -> value.status() == CombatEncounterStatus.COMPLETED)
            .orElseThrow(() -> new IllegalArgumentException("Unknown combat encounter: " + combatId));
    }
}
