package com.beyondsignal.game.presentation.integration;

import com.beyondsignal.game.combat.engine.CombatEncounter;
import com.beyondsignal.game.combat.engine.CombatTickResult;

@FunctionalInterface
public interface CombatTickPresentationHook {
    void onTick(CombatEncounter encounter, CombatTickResult tickResult);

    static CombatTickPresentationHook noOp() {
        return (encounter, tickResult) -> {
            // Intentionally empty.
        };
    }
}
