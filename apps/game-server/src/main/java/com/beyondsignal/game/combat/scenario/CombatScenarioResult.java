package com.beyondsignal.game.combat.scenario;

import com.beyondsignal.game.combat.engine.CombatEncounterStatus;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.replay.CombatReplay;
import com.beyondsignal.game.combat.stats.CombatStatistics;
import java.util.Optional;

public record CombatScenarioResult(
    long ticksExecuted,
    CombatEncounterStatus encounterStatus,
    CombatSide winningSide,
    CombatStatistics statistics,
    CombatReplay replay
) {
    public Optional<CombatSide> winner() {
        return Optional.ofNullable(winningSide);
    }
}
