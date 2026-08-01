package com.beyondsignal.game.combat.ai.snapshot;

import com.beyondsignal.game.combat.engine.CombatEncounter;
import com.beyondsignal.game.combat.engine.CombatParticipant;
import com.beyondsignal.game.combat.shield.ShieldQuadrant;
import java.util.EnumMap;
import java.util.Objects;

public final class CombatSnapshotFactory {
    public CombatAiSnapshot create(CombatEncounter encounter) {
        Objects.requireNonNull(encounter, "encounter");

        return new CombatAiSnapshot(
            encounter.combatId(),
            encounter.context().clock().currentTick(),
            encounter.participants().stream()
                .map(this::combatant)
                .toList()
        );
    }

    private CombatantSnapshot combatant(CombatParticipant participant) {
        EnumMap<ShieldQuadrant, Integer> strength = new EnumMap<>(ShieldQuadrant.class);
        EnumMap<ShieldQuadrant, Integer> maximum = new EnumMap<>(ShieldQuadrant.class);

        for (ShieldQuadrant quadrant : ShieldQuadrant.values()) {
            var state = participant.shields().state(quadrant);
            strength.put(quadrant, state.strength());
            maximum.put(quadrant, state.maximumStrength());
        }

        return new CombatantSnapshot(
            participant.participantId(),
            participant.side(),
            participant.status(),
            participant.hull(),
            participant.maximumHull(),
            new ShieldSnapshot(strength, maximum),
            participant.weapons().stream()
                .map(weapon -> new WeaponSnapshot(
                    weapon.mountId(),
                    weapon.definition().id(),
                    weapon.definition().type(),
                    weapon.readyToFire(),
                    weapon.cooldownRemainingTicks(),
                    weapon.ammunitionRemaining(),
                    weapon.definition().baseDamage(),
                    weapon.definition().maximumRange()
                ))
                .toList(),
            participant.selectedTargetId().orElse(null),
            participant.targetingQuality(),
            participant.evasion()
        );
    }
}
