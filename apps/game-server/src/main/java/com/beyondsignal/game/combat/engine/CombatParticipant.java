package com.beyondsignal.game.combat.engine;

import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.shield.ShieldModel;
import com.beyondsignal.game.combat.weapon.WeaponMountState;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Mutable encounter-owned combatant state.
 */
public final class CombatParticipant {
    private final UUID participantId;
    private final CombatSide side;
    private final ShieldModel shields;
    private final List<WeaponMountState> weapons;
    private final int maximumHull;

    private int hull;
    private CombatParticipantStatus status;
    private UUID selectedTargetId;
    private double targetingQuality;
    private double evasion;

    public CombatParticipant(
        UUID participantId,
        CombatSide side,
        ShieldModel shields,
        List<WeaponMountState> weapons,
        int maximumHull,
        double targetingQuality,
        double evasion
    ) {
        this.participantId = Objects.requireNonNull(participantId, "participantId");
        this.side = Objects.requireNonNull(side, "side");
        this.shields = Objects.requireNonNull(shields, "shields");
        this.weapons = new ArrayList<>(Objects.requireNonNull(weapons, "weapons"));
        if (maximumHull < 1) {
            throw new IllegalArgumentException("maximumHull must be positive");
        }
        validateUnit(targetingQuality, "targetingQuality");
        validateUnit(evasion, "evasion");

        this.maximumHull = maximumHull;
        this.hull = maximumHull;
        this.status = CombatParticipantStatus.ACTIVE;
        this.targetingQuality = targetingQuality;
        this.evasion = evasion;
    }

    public UUID participantId() {
        return participantId;
    }

    public CombatSide side() {
        return side;
    }

    public ShieldModel shields() {
        return shields;
    }

    public synchronized List<WeaponMountState> weapons() {
        return List.copyOf(weapons);
    }

    public synchronized Optional<WeaponMountState> weapon(UUID mountId) {
        return weapons.stream()
            .filter(weapon -> weapon.mountId().equals(mountId))
            .findFirst();
    }

    public synchronized void replaceWeapon(WeaponMountState replacement) {
        Objects.requireNonNull(replacement, "replacement");
        for (int index = 0; index < weapons.size(); index++) {
            if (weapons.get(index).mountId().equals(replacement.mountId())) {
                weapons.set(index, replacement);
                return;
            }
        }
        throw new IllegalArgumentException("Unknown weapon mount: " + replacement.mountId());
    }

    public synchronized void tickWeapons() {
        for (int index = 0; index < weapons.size(); index++) {
            weapons.set(index, weapons.get(index).tick());
        }
    }

    public int hull() {
        return hull;
    }

    public int maximumHull() {
        return maximumHull;
    }

    public CombatParticipantStatus status() {
        return status;
    }

    public Optional<UUID> selectedTargetId() {
        return Optional.ofNullable(selectedTargetId);
    }

    public double targetingQuality() {
        return targetingQuality;
    }

    public double evasion() {
        return evasion;
    }

    public boolean operational() {
        return status == CombatParticipantStatus.ACTIVE
            || status == CombatParticipantStatus.RETREATING;
    }

    public void selectTarget(UUID targetId) {
        if (!operational()) {
            throw new IllegalStateException("Participant is not operational");
        }
        selectedTargetId = Objects.requireNonNull(targetId, "targetId");
    }

    public void clearTarget() {
        selectedTargetId = null;
    }

    public void applyHullDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("damage cannot be negative");
        }
        hull = Math.max(0, hull - damage);
        if (hull == 0) {
            status = CombatParticipantStatus.DESTROYED;
            selectedTargetId = null;
        }
    }

    public void beginRetreat() {
        if (status != CombatParticipantStatus.ACTIVE) {
            throw new IllegalStateException("Only active participants can retreat");
        }
        status = CombatParticipantStatus.RETREATING;
    }

    public void disable() {
        if (status != CombatParticipantStatus.DESTROYED) {
            status = CombatParticipantStatus.DISABLED;
        }
    }

    public void setTargetingQuality(double value) {
        validateUnit(value, "targetingQuality");
        targetingQuality = value;
    }

    public void setEvasion(double value) {
        validateUnit(value, "evasion");
        evasion = value;
    }

    private static void validateUnit(double value, String name) {
        if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(name + " must be between 0.0 and 1.0");
        }
    }
}
