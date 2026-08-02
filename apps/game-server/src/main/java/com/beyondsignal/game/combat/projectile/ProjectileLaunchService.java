package com.beyondsignal.game.combat.projectile;

import com.beyondsignal.game.combat.engine.CombatEncounter;
import com.beyondsignal.game.combat.engine.FireSolution;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class ProjectileLaunchService {
    private final ProjectileFlightModel flightModel;

    public ProjectileLaunchService() {
        this(new ProjectileFlightModel());
    }

    public ProjectileLaunchService(ProjectileFlightModel flightModel) {
        this.flightModel = java.util.Objects.requireNonNull(
            flightModel,
            "flightModel"
        );
    }

    public ProjectileState launch(
        CombatEncounter encounter,
        FireSolution solution
    ) {
        long launchTick = encounter.context().clock().currentTick();
        long impactTick = launchTick + flightModel.travelTicks(
            solution.distance()
        );
        UUID projectileId = UUID.nameUUIDFromBytes(
            (
                encounter.combatId().value()
                    + ":"
                    + solution.attackerId()
                    + ":"
                    + solution.weapon().mountId()
                    + ":"
                    + launchTick
            ).getBytes(StandardCharsets.UTF_8)
        );

        ProjectileState projectile = ProjectileState.inFlight(
            projectileId,
            solution.attackerId(),
            solution.targetId(),
            solution.weapon().definition().id(),
            solution.weapon().definition().damageType(),
            solution.weapon().definition().baseDamage(),
            launchTick,
            impactTick,
            solution.hit()
        );
        encounter.addProjectile(projectile);
        return projectile;
    }
}
