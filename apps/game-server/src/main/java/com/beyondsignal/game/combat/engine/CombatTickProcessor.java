package com.beyondsignal.game.combat.engine;

import com.beyondsignal.game.combat.command.CombatCommand;
import com.beyondsignal.game.combat.event.CombatEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Deterministic combat tick processor.
 */
public final class CombatTickProcessor {
    private final CombatCommandProcessor commandProcessor;

    public CombatTickProcessor() {
        this(new CombatCommandProcessor());
    }

    public CombatTickProcessor(CombatCommandProcessor commandProcessor) {
        this.commandProcessor = Objects.requireNonNull(commandProcessor, "commandProcessor");
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
            List<CombatEvent> commandEvents = commandProcessor.process(encounter, command);
            for (CombatEvent event : commandEvents) {
                if (!encounter.events().contains(event)) {
                    encounter.appendEvent(event);
                }
            }
            produced.addAll(commandEvents);
        }

        encounter.evaluateCompletion();

        return new CombatTickResult(
            tick,
            commands.size(),
            produced,
            encounter.status()
        );
    }
}
