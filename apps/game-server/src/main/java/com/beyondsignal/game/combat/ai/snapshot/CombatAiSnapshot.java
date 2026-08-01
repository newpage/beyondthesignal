package com.beyondsignal.game.combat.ai.snapshot;

import com.beyondsignal.game.combat.model.CombatId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record CombatAiSnapshot(
    CombatId combatId,
    long tick,
    List<CombatantSnapshot> combatants
) {
    public CombatAiSnapshot {
        combatId = Objects.requireNonNull(combatId, "combatId");
        if (tick < 0) {
            throw new IllegalArgumentException("tick cannot be negative");
        }
        combatants = List.copyOf(Objects.requireNonNull(combatants, "combatants"));
    }

    public Optional<CombatantSnapshot> combatant(UUID id) {
        return combatants.stream()
            .filter(combatant -> combatant.participantId().equals(id))
            .findFirst();
    }
}
