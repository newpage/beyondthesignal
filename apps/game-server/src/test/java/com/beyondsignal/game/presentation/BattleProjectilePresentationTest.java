package com.beyondsignal.game.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.projectile.ProjectileState;
import com.beyondsignal.game.combat.weapon.DamageType;
import com.beyondsignal.game.presentation.context.PresentationConfiguration;
import com.beyondsignal.game.presentation.context.PresentationContext;
import com.beyondsignal.game.presentation.context.PresentationDebugOptions;
import com.beyondsignal.game.presentation.mapper.BattlePresentationMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BattleProjectilePresentationTest {
    @Test
    void mapsActiveProjectileProgress() {
        UUID source = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        ProjectileState projectile = ProjectileState.inFlight(
            UUID.randomUUID(),
            source,
            target,
            "torpedo",
            DamageType.KINETIC,
            20,
            2,
            10,
            true
        );

        var frame = new BattlePresentationMapper().map(
            new PresentationContext(
                new CombatAiSnapshot(CombatId.random(), 6, List.of()),
                1,
                1,
                Instant.EPOCH,
                PresentationConfiguration.defaults(),
                PresentationDebugOptions.disabled(),
                List.of(),
                List.of(projectile)
            )
        );

        assertEquals(1, frame.projectiles().size());
        assertEquals(0.5, frame.projectiles().getFirst().progress());
    }
}
