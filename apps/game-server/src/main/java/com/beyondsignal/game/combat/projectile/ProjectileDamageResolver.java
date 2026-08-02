package com.beyondsignal.game.combat.projectile;

import com.beyondsignal.game.combat.engine.CombatParticipant;
import com.beyondsignal.game.combat.engine.CombatParticipantStatus;
import com.beyondsignal.game.combat.shield.ShieldImpact;
import com.beyondsignal.game.combat.shield.ShieldQuadrant;
import java.util.Objects;

public final class ProjectileDamageResolver {
    public ProjectileDamageResolution resolve(
        CombatParticipant target,
        ProjectileState projectile
    ) {
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(projectile, "projectile");

        ShieldQuadrant quadrant = ShieldQuadrant.FORWARD;
        int strengthBefore = target.shields().state(quadrant).strength();
        ShieldImpact impact = target.shields().apply(
            quadrant,
            projectile.damageType(),
            projectile.baseDamage(),
            1.0
        );

        int hullBefore = target.hull();
        if (impact.penetratingDamage() > 0) {
            target.applyHullDamage(impact.penetratingDamage());
        }

        return new ProjectileDamageResolution(
            impact,
            hullBefore - target.hull(),
            target.hull(),
            strengthBefore > 0 && impact.collapsed(),
            target.status() == CombatParticipantStatus.DESTROYED
        );
    }
}
