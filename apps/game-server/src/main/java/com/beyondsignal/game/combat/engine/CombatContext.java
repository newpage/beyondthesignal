package com.beyondsignal.game.combat.engine;

import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.rng.CombatRandom;
import java.util.Objects;

/**
 * Shared immutable dependencies for one combat encounter.
 */
public record CombatContext(
    CombatId combatId,
    long seed,
    CombatRandom random,
    CombatClock clock
) {
    public CombatContext {
        combatId = Objects.requireNonNull(combatId, "combatId");
        random = Objects.requireNonNull(random, "random");
        clock = Objects.requireNonNull(clock, "clock");
    }
}
