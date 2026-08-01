package com.beyondsignal.game.combat.ai.sensor;

import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.CombatantSnapshot;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class CombatSensorSuite {
    public CombatantSnapshot self(CombatAiSnapshot snapshot, UUID selfId) {
        Objects.requireNonNull(snapshot, "snapshot");
        Objects.requireNonNull(selfId, "selfId");
        return snapshot.combatant(selfId)
            .orElseThrow(() -> new IllegalArgumentException("Unknown AI combatant: " + selfId));
    }

    public List<CombatantSnapshot> hostiles(CombatAiSnapshot snapshot, UUID selfId) {
        CombatantSnapshot self = self(snapshot, selfId);
        return snapshot.combatants().stream()
            .filter(combatant -> combatant.status() !=
                com.beyondsignal.game.combat.engine.CombatParticipantStatus.DESTROYED)
            .filter(combatant -> combatant.side() != self.side())
            .filter(combatant -> combatant.side() !=
                com.beyondsignal.game.combat.model.CombatSide.NEUTRAL)
            .sorted(Comparator.comparing(CombatantSnapshot::participantId))
            .toList();
    }

    public Optional<CombatantSnapshot> weakestHullHostile(
        CombatAiSnapshot snapshot,
        UUID selfId
    ) {
        return hostiles(snapshot, selfId).stream()
            .min(Comparator
                .comparingDouble(CombatantSnapshot::hullPercentage)
                .thenComparing(CombatantSnapshot::participantId));
    }

    public Optional<CombatantSnapshot> weakestShieldHostile(
        CombatAiSnapshot snapshot,
        UUID selfId
    ) {
        return hostiles(snapshot, selfId).stream()
            .min(Comparator
                .<CombatantSnapshot>comparingDouble(combatant -> combatant.shields().percentage())
                .thenComparing(CombatantSnapshot::participantId));
    }
}
