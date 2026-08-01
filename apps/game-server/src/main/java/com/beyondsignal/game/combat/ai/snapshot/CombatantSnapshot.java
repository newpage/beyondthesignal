package com.beyondsignal.game.combat.ai.snapshot;

import com.beyondsignal.game.combat.engine.CombatParticipantStatus;
import com.beyondsignal.game.combat.model.CombatSide;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record CombatantSnapshot(
    UUID participantId,
    CombatSide side,
    CombatParticipantStatus status,
    int hull,
    int maximumHull,
    ShieldSnapshot shields,
    List<WeaponSnapshot> weapons,
    UUID selectedTargetId,
    double targetingQuality,
    double evasion
) {
    public CombatantSnapshot {
        participantId = Objects.requireNonNull(participantId, "participantId");
        side = Objects.requireNonNull(side, "side");
        status = Objects.requireNonNull(status, "status");
        shields = Objects.requireNonNull(shields, "shields");
        weapons = List.copyOf(Objects.requireNonNull(weapons, "weapons"));
        if (hull < 0 || maximumHull < 1 || hull > maximumHull) {
            throw new IllegalArgumentException("Invalid hull values");
        }
        validateUnit(targetingQuality, "targetingQuality");
        validateUnit(evasion, "evasion");
    }

    public Optional<UUID> selectedTarget() {
        return Optional.ofNullable(selectedTargetId);
    }

    public double hullPercentage() {
        return hull / (double) maximumHull;
    }

    public long readyWeaponCount() {
        return weapons.stream().filter(WeaponSnapshot::ready).count();
    }

    public int readyWeaponDamage() {
        return weapons.stream()
            .filter(WeaponSnapshot::ready)
            .mapToInt(WeaponSnapshot::baseDamage)
            .sum();
    }

    private static void validateUnit(double value, String name) {
        if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
    }
}
