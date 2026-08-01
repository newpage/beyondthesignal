package com.beyondsignal.game.combat.command;

import com.beyondsignal.game.combat.model.CombatId;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class FireCombatWeaponCommand extends CombatCommand {
    private final UUID weaponMountId;
    private final double distance;
    private final double powerModifier;

    public FireCombatWeaponCommand(
        CombatId combatId,
        UUID actorId,
        UUID weaponMountId,
        double distance,
        double powerModifier,
        Instant issuedAt
    ) {
        super(combatId, actorId, issuedAt);
        this.weaponMountId = Objects.requireNonNull(weaponMountId, "weaponMountId");
        if (!Double.isFinite(distance) || distance < 0.0) {
            throw new IllegalArgumentException("distance must be finite and non-negative");
        }
        if (!Double.isFinite(powerModifier) || powerModifier <= 0.0) {
            throw new IllegalArgumentException("powerModifier must be positive");
        }
        this.distance = distance;
        this.powerModifier = powerModifier;
    }

    public UUID weaponMountId() {
        return weaponMountId;
    }

    public double distance() {
        return distance;
    }

    public double powerModifier() {
        return powerModifier;
    }
}
