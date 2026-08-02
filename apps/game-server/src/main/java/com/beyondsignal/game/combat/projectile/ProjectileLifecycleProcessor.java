package com.beyondsignal.game.combat.projectile;

import com.beyondsignal.game.combat.engine.CombatEncounter;
import com.beyondsignal.game.combat.engine.CombatEventFactory;
import com.beyondsignal.game.combat.event.CombatEvent;
import com.beyondsignal.game.combat.event.CombatEventType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ProjectileLifecycleProcessor {
    private final ProjectileInterceptor interceptor;
    private final ProjectileDamageResolver damageResolver;
    private final CombatEventFactory eventFactory;

    public ProjectileLifecycleProcessor() {
        this(
            ProjectileInterceptor.none(),
            new ProjectileDamageResolver(),
            new CombatEventFactory()
        );
    }

    public ProjectileLifecycleProcessor(
        ProjectileInterceptor interceptor,
        ProjectileDamageResolver damageResolver,
        CombatEventFactory eventFactory
    ) {
        this.interceptor = Objects.requireNonNull(interceptor, "interceptor");
        this.damageResolver = Objects.requireNonNull(
            damageResolver,
            "damageResolver"
        );
        this.eventFactory = Objects.requireNonNull(
            eventFactory,
            "eventFactory"
        );
    }

    public List<CombatEvent> advance(
        CombatEncounter encounter,
        long tick
    ) {
        List<CombatEvent> events = new ArrayList<>();

        for (ProjectileState projectile : encounter.projectiles()) {
            if (projectile.status() != ProjectileStatus.IN_FLIGHT) {
                continue;
            }

            if (interceptor.intercept(encounter, projectile, tick)) {
                encounter.replaceProjectile(
                    projectile.withStatus(ProjectileStatus.INTERCEPTED)
                );
                events.add(eventFactory.projectileExpired(
                    encounter,
                    projectile,
                    "INTERCEPTED"
                ));
                continue;
            }

            if (!projectile.due(tick)) {
                continue;
            }

            var target = encounter.participant(projectile.targetId());
            if (target.isEmpty() || !target.get().operational()) {
                encounter.replaceProjectile(
                    projectile.withStatus(ProjectileStatus.EXPIRED)
                );
                events.add(eventFactory.projectileExpired(
                    encounter,
                    projectile,
                    "TARGET_UNAVAILABLE"
                ));
                continue;
            }

            if (!projectile.predictedHit()) {
                encounter.replaceProjectile(
                    projectile.withStatus(ProjectileStatus.EXPIRED)
                );
                events.add(eventFactory.projectileExpired(
                    encounter,
                    projectile,
                    "MISSED"
                ));
                continue;
            }

            ProjectileDamageResolution resolution = damageResolver.resolve(
                target.get(),
                projectile
            );
            encounter.replaceProjectile(
                projectile.withStatus(ProjectileStatus.IMPACTED)
            );
            events.add(eventFactory.projectileImpact(encounter, projectile));
            events.add(eventFactory.projectileShieldImpact(
                encounter,
                projectile,
                resolution
            ));
            if (resolution.hullDamage() > 0) {
                events.add(eventFactory.projectileHullDamage(
                    encounter,
                    projectile,
                    resolution
                ));
            }
            if (resolution.targetDestroyed()) {
                events.add(eventFactory.projectileDestroyedShip(
                    encounter,
                    projectile
                ));
            }
        }

        return List.copyOf(events);
    }
}
