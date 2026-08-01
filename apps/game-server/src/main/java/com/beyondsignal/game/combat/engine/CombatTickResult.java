package com.beyondsignal.game.combat.engine;

import com.beyondsignal.game.combat.event.CombatEvent;
import java.util.List;

public record CombatTickResult(
    long tick,
    int commandsProcessed,
    List<CombatEvent> eventsProduced,
    CombatEncounterStatus encounterStatus
) {
    public CombatTickResult {
        if (tick < 1) {
            throw new IllegalArgumentException("tick must be positive");
        }
        if (commandsProcessed < 0) {
            throw new IllegalArgumentException("commandsProcessed cannot be negative");
        }
        eventsProduced = List.copyOf(eventsProduced);
    }
}
