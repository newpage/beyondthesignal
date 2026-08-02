package com.beyondsignal.game.combat.projectile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beyondsignal.game.combat.engine.CombatContext;
import com.beyondsignal.game.combat.engine.CombatClock;
import com.beyondsignal.game.combat.engine.CombatEncounter;
import com.beyondsignal.game.combat.engine.CombatEventFactory;
import com.beyondsignal.game.combat.engine.CombatParticipant;
import com.beyondsignal.game.combat.event.CombatEventType;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.rng.SplitMix64CombatRandom;
import com.beyondsignal.game.combat.shield.ShieldModel;
import com.beyondsignal.game.combat.weapon.DamageType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProjectileLifecycleProcessorTest {
    @Test
    void resolvesImpactOnlyWhenTravelCompletes() {
        CombatId id = CombatId.random();
        CombatEncounter encounter = new CombatEncounter(new CombatContext(
            id,
            5,
            new SplitMix64CombatRandom(5),
            new CombatClock()
        ));
        UUID source = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        encounter.addParticipant(participant(source, CombatSide.FRIENDLY));
        encounter.addParticipant(participant(target, CombatSide.HOSTILE));
        encounter.start();

        ProjectileState projectile = ProjectileState.inFlight(
            UUID.randomUUID(),
            source,
            target,
            "torpedo",
            DamageType.KINETIC,
            25,
            1,
            4,
            true
        );
        encounter.addProjectile(projectile);

        ProjectileLifecycleProcessor processor =
            new ProjectileLifecycleProcessor();

        assertTrue(processor.advance(encounter, 3).isEmpty());
        var events = processor.advance(encounter, 4);
        assertEquals(CombatEventType.PROJECTILE_IMPACT, events.getFirst().type());
        assertTrue(events.stream().anyMatch(
            event -> event.type() == CombatEventType.SHIELD_IMPACT
        ));
    }

    private static CombatParticipant participant(
        UUID id,
        CombatSide side
    ) {
        return new CombatParticipant(
            id,
            side,
            ShieldModel.uniform(50, 0),
            List.of(),
            100,
            0.5,
            0.0
        );
    }
}
