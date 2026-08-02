package com.beyondsignal.game.combat.engine;

import com.beyondsignal.game.combat.command.CombatCommand;
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

    public CombatSimulationEngine() {
        this(new CombatTickProcessor(), CombatTickPresentationHook.noOp());
    }

    public CombatSimulationEngine(CombatTickProcessor tickProcessor) {
        this(tickProcessor, CombatTickPresentationHook.noOp());
    }

    public CombatSimulationEngine(
        CombatTickProcessor tickProcessor,
        CombatTickPresentationHook presentationHook
    ) {
        this.tickProcessor = Objects.requireNonNull(
            tickProcessor,
            "tickProcessor"
        );
        this.presentationHook = Objects.requireNonNull(
            presentationHook,
            "presentationHook"
        );
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
        presentationHook.onTick(encounter, result);
        return result;
    }

    public synchronized boolean completed(CombatId combatId) {
        return encounter(combatId)
            .map(value -> value.status() == CombatEncounterStatus.COMPLETED)
            .orElseThrow(() -> new IllegalArgumentException("Unknown combat encounter: " + combatId));
    }
}
