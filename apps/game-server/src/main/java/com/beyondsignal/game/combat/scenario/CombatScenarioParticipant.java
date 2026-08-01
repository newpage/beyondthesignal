package com.beyondsignal.game.combat.scenario;

import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.weapon.WeaponDefinition;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record CombatScenarioParticipant(
    UUID participantId,
    CombatSide side,
    int hull,
    int shieldStrength,
    double targetingQuality,
    double evasion,
    List<WeaponDefinition> weapons
) {
    public CombatScenarioParticipant {
        participantId = Objects.requireNonNull(participantId, "participantId");
        side = Objects.requireNonNull(side, "side");
        if (hull < 1 || shieldStrength < 0) {
            throw new IllegalArgumentException("Invalid hull or shield strength");
        }
        validateUnit(targetingQuality, "targetingQuality");
        validateUnit(evasion, "evasion");
        weapons = List.copyOf(Objects.requireNonNull(weapons, "weapons"));
    }

    private static void validateUnit(double value, String name) {
        if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
    }
}
