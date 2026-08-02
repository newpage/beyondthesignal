package com.beyondsignal.game.combat.engine;

import com.beyondsignal.game.combat.command.CombatCommand;
import com.beyondsignal.game.combat.event.CombatEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Deterministic combat tick processor.
 *
 * <p>The processor is the single authority that assigns final event sequence
 * numbers. Command processors may create several immutable events before any
 * one of them is appended to the encounter log, so their provisional sequence
 * values cannot be trusted for a batch.</p>
 */
public final class CombatTickProcessor {
    private final CombatCommandProcessor commandProcessor;

    public CombatTickProcessor() {
        this(new CombatCommandProcessor());
    }

    public CombatTickProcessor(CombatCommandProcessor commandProcessor) {
        this.commandProcessor = Objects.requireNonNull(
            commandProcessor,
            "commandProcessor"
        );
    }

    public CombatTickResult process(CombatEncounter encounter) {
        Objects.requireNonNull(encounter, "encounter");
        if (encounter.status() != CombatEncounterStatus.ACTIVE) {
            throw new IllegalStateException("Encounter is not active");
        }

        long tick = encounter.context().clock().advance();

        for (CombatParticipant participant : encounter.participants()) {
            participant.tickWeapons();
            participant.shields().regenerate();
        }

        List<CombatCommand> commands = encounter.drainCommands();
        List<CombatEvent> produced = new ArrayList<>();

        for (CombatCommand command : commands) {
            List<CombatEvent> commandEvents =
                commandProcessor.process(encounter, command);

            for (CombatEvent event : commandEvents) {
                CombatEvent sequenced = withSequence(
                    event,
                    encounter.nextEventSequence()
                );
                encounter.appendEvent(sequenced);
                produced.add(sequenced);
            }
        }

        encounter.evaluateCompletion();

        return new CombatTickResult(
            tick,
            commands.size(),
            produced,
            encounter.status()
        );
    }

    private static CombatEvent withSequence(
        CombatEvent event,
        long sequence
    ) {
        return new CombatEvent(
            event.combatId(),
            sequence,
            event.tick(),
            event.sourceId(),
            event.targetId(),
            event.type(),
            event.occurredAt(),
            event.payload()
        );
    }
}
