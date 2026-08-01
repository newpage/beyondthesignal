package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.Set;
import java.util.UUID;

public record FormationCasualtyUpdate(Set<UUID> unavailableParticipantIds) {
    public FormationCasualtyUpdate {
        unavailableParticipantIds = Set.copyOf(unavailableParticipantIds);
    }

    public boolean hasLosses() {
        return !unavailableParticipantIds.isEmpty();
    }
}
