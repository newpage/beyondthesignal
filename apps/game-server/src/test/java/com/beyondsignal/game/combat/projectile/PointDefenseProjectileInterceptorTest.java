package com.beyondsignal.game.combat.projectile;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beyondsignal.game.combat.engine.CombatContext;
import com.beyondsignal.game.combat.engine.CombatClock;
import com.beyondsignal.game.combat.engine.CombatEncounter;
import com.beyondsignal.game.combat.engine.CombatParticipant;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.rng.SplitMix64CombatRandom;
import com.beyondsignal.game.combat.shield.ShieldModel;
import com.beyondsignal.game.combat.weapon.DamageType;
import com.beyondsignal.game.combat.weapon.WeaponArc;
import com.beyondsignal.game.combat.weapon.WeaponDefinition;
import com.beyondsignal.game.combat.weapon.WeaponMountState;
import com.beyondsignal.game.combat.weapon.WeaponType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PointDefenseProjectileInterceptorTest {
    @Test
    void guaranteedPointDefenseConsumesReadyBeam() {
        CombatEncounter encounter = new CombatEncounter(new CombatContext(
            CombatId.random(),
            7,
            new SplitMix64CombatRandom(7),
            new CombatClock()
        ));
        UUID source = UUID.randomUUID();
        UUID target = UUID.randomUUID();
        UUID mount = UUID.randomUUID();

        encounter.addParticipant(new CombatParticipant(
            source,
            CombatSide.HOSTILE,
            ShieldModel.uniform(50, 0),
            List.of(),
            100,
            0.5,
            0.0
        ));
        CombatParticipant defender = new CombatParticipant(
            target,
            CombatSide.FRIENDLY,
            ShieldModel.uniform(50, 0),
            List.of(WeaponMountState.ready(mount, beam(), 0)),
            100,
            1.0,
            0.0
        );
        encounter.addParticipant(defender);
        encounter.start();

        ProjectileState projectile = ProjectileState.inFlight(
            UUID.randomUUID(),
            source,
            target,
            "torpedo",
            DamageType.KINETIC,
            20,
            1,
            10,
            true
        );

        assertTrue(new PointDefenseProjectileInterceptor(1.0)
            .intercept(encounter, projectile, 2));
        assertTrue(defender.weapon(mount).orElseThrow().cooldownRemainingTicks() > 0);
    }

    private static WeaponDefinition beam() {
        return new WeaponDefinition(
            "pd",
            "Point Defense",
            WeaponType.BEAM,
            DamageType.ENERGY,
            WeaponArc.OMNIDIRECTIONAL,
            5,
            2_000,
            1.0,
            0,
            3,
            0
        );
    }
}
