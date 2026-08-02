package com.beyondsignal.game.combat.fleet.squadron;

import com.beyondsignal.game.combat.engine.CombatEncounter;
import com.beyondsignal.game.combat.engine.CombatParticipant;
import com.beyondsignal.game.combat.fleet.ai.FleetDecision;
import java.util.Objects;
import java.util.UUID;

public final class SquadronTargetSelector {
    public UUID select(
        CombatEncounter encounter,
        FleetDecision decision
    ) {
        Objects.requireNonNull(encounter, "encounter");
        Objects.requireNonNull(decision, "decision");

        UUID primary = decision.primaryTargetId();
        if (isOperational(encounter, primary)) {
            return primary;
        }

        return decision.threats().stream()
            .map(threat -> threat.participantId())
            .filter(target -> isOperational(encounter, target))
            .findFirst()
            .orElse(null);
    }

    private static boolean isOperational(
        CombatEncounter encounter,
        UUID participantId
    ) {
        return participantId != null
            && encounter.participant(participantId)
                .filter(CombatParticipant::operational)
                .isPresent();
    }
}
