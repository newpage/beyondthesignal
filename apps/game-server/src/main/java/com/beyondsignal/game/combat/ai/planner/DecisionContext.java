package com.beyondsignal.game.combat.ai.planner;

import com.beyondsignal.game.combat.ai.evaluation.ThreatTable;
import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.CombatantSnapshot;
import java.util.Objects;

public record DecisionContext(
    CombatAiSnapshot snapshot,
    CombatantSnapshot self,
    ThreatTable threats
) {
    public DecisionContext {
        snapshot = Objects.requireNonNull(snapshot, "snapshot");
        self = Objects.requireNonNull(self, "self");
        threats = Objects.requireNonNull(threats, "threats");
    }
}
