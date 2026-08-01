package com.beyondsignal.game.combat.engine;

import java.util.Objects;
import java.util.UUID;

public final class TargetManager {
    public void selectTarget(
        CombatEncounter encounter,
        CombatParticipant actor,
        UUID targetId
    ) {
        Objects.requireNonNull(encounter, "encounter");
        Objects.requireNonNull(actor, "actor");
        Objects.requireNonNull(targetId, "targetId");

        CombatParticipant target = encounter.participant(targetId)
            .orElseThrow(() -> new IllegalArgumentException("Unknown target: " + targetId));

        if (!target.operational()) {
            throw new IllegalStateException("Target is not operational");
        }
        if (actor.participantId().equals(targetId)) {
            throw new IllegalArgumentException("Participant cannot target itself");
        }
        if (actor.side() == target.side()) {
            throw new IllegalArgumentException("Friendly targeting is not allowed");
        }

        actor.selectTarget(targetId);
    }
}
