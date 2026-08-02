package com.beyondsignal.game.combat.fleet.squadron;

import com.beyondsignal.game.combat.engine.CombatClock;
import com.beyondsignal.game.combat.engine.CombatContext;
import com.beyondsignal.game.combat.engine.CombatEncounter;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.rng.SplitMix64CombatRandom;

final class FleetSquadronTestSupport {
    private FleetSquadronTestSupport() {}

    static CombatEncounter encounter() {
        CombatId id = CombatId.random();
        return new CombatEncounter(new CombatContext(
            id,
            17,
            new SplitMix64CombatRandom(17),
            new CombatClock()
        ));
    }
}
