package com.beyondsignal.game.combat.engine;

import com.beyondsignal.game.combat.shield.ShieldImpact;
import com.beyondsignal.game.combat.shield.ShieldQuadrant;
import com.beyondsignal.game.combat.weapon.WeaponArc;
import com.beyondsignal.game.combat.weapon.WeaponDefinition;
import java.util.Objects;

public final class BeamDamageResolver {
    public BeamDamageResolution resolve(
        CombatParticipant target,
        WeaponDefinition weapon
    ) {
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(weapon, "weapon");

        ShieldQuadrant quadrant = impactQuadrant(weapon.arc());
        int strengthBefore = target.shields().state(quadrant).strength();

        ShieldImpact impact = target.shields().apply(
            quadrant,
            weapon.damageType(),
            weapon.baseDamage(),
            1.0
        );

        int hullBefore = target.hull();
        if (impact.penetratingDamage() > 0) {
            target.applyHullDamage(impact.penetratingDamage());
        }

        return new BeamDamageResolution(
            impact,
            hullBefore - target.hull(),
            target.hull(),
            strengthBefore > 0 && impact.collapsed(),
            target.status() == CombatParticipantStatus.DESTROYED
        );
    }

    private static ShieldQuadrant impactQuadrant(WeaponArc arc) {
        return switch (arc) {
            case FORWARD, OMNIDIRECTIONAL -> ShieldQuadrant.FORWARD;
            case AFT -> ShieldQuadrant.AFT;
            case PORT -> ShieldQuadrant.PORT;
            case STARBOARD -> ShieldQuadrant.STARBOARD;
        };
    }
}
