package com.beyondsignal.game.combat.engine;

import com.beyondsignal.game.combat.command.CombatCommand;
import com.beyondsignal.game.combat.event.CombatEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Deterministic baseline tick processor.
 *
 * <p>Part 3A advances passive state and drains commands. Command-specific combat
 * resolution is added in Part 3B.</p>
 */
public final class CombatTickProcessor {
    public CombatTickResult process(CombatEncounter encounter) {
        Objects.requireNonNull(encounter, "encounter");
        if (encounter.status() != CombatEncounterStatus.ACTIVE) {
            throw new IllegalStateException("Encounter is not active");
        }

        long tick = encounter.context().clock().advance();

        for (CombatParticipant participant : encounter.participants()) {
            participant.shields().regenerate();
        }

        List<CombatCommand> commands = encounter.drainCommands();
        List<CombatEvent> produced = new ArrayList<>();

        encounter.evaluateCompletion();

        return new CombatTickResult(
            tick,
            commands.size(),
            produced,
            encounter.status()
        );
    }
}
