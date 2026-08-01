package com.beyondsignal.game.combat.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.command.FireCombatWeaponCommand;
import com.beyondsignal.game.combat.command.SelectCombatTargetCommand;
import com.beyondsignal.game.combat.event.CombatEventType;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.shield.ShieldModel;
import com.beyondsignal.game.combat.weapon.DamageType;
import com.beyondsignal.game.combat.weapon.WeaponArc;
import com.beyondsignal.game.combat.weapon.WeaponDefinition;
import com.beyondsignal.game.combat.weapon.WeaponMountState;
import com.beyondsignal.game.combat.weapon.WeaponType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatFireIntegrationTest {
    @Test
    void selectAndFireProducesDeterministicOrderedEvents() {
        CombatSimulationEngine engine = new CombatSimulationEngine();
        CombatId combatId = CombatId.fromString("00000000-0000-0000-0000-000000000101");
        CombatEncounter encounter = engine.createEncounter(combatId, 42L);

        UUID attackerId = UUID.fromString("00000000-0000-0000-0000-000000000201");
        UUID targetId = UUID.fromString("00000000-0000-0000-0000-000000000202");
        UUID mountId = UUID.fromString("00000000-0000-0000-0000-000000000301");

        CombatParticipant attacker = new CombatParticipant(
            attackerId,
            CombatSide.FRIENDLY,
            ShieldModel.uniform(50, 1),
            List.of(WeaponMountState.ready(mountId, phaser(), 0)),
            100,
            1.0,
            0.0
        );
        CombatParticipant target = new CombatParticipant(
            targetId,
            CombatSide.HOSTILE,
            ShieldModel.uniform(50, 1),
            List.of(),
            100,
            0.5,
            0.0
        );

        encounter.addParticipant(attacker);
        encounter.addParticipant(target);
        encounter.start();

        engine.submit(new SelectCombatTargetCommand(
            combatId, attackerId, targetId, Instant.EPOCH
        ));
        engine.submit(new FireCombatWeaponCommand(
            combatId, attackerId, mountId, 1000.0, 1.0, Instant.EPOCH
        ));

        CombatTickResult result = engine.tick(combatId);

        assertEquals(3, result.eventsProduced().size());
        assertEquals(CombatEventType.TARGET_SELECTED, result.eventsProduced().get(0).type());
        assertEquals(CombatEventType.WEAPON_FIRED, result.eventsProduced().get(1).type());
        assertTrue(
            result.eventsProduced().get(2).type() == CombatEventType.WEAPON_HIT
                || result.eventsProduced().get(2).type() == CombatEventType.WEAPON_MISSED
        );
        assertEquals(3, encounter.events().size());
        assertEquals(1L, encounter.events().get(0).sequence());
        assertEquals(2L, encounter.events().get(1).sequence());
        assertEquals(3L, encounter.events().get(2).sequence());
        assertEquals(5, attacker.weapon(mountId).orElseThrow().cooldownRemainingTicks());
    }

    private static WeaponDefinition phaser() {
        return new WeaponDefinition(
            "phaser-mk1",
            "Phaser Mk I",
            WeaponType.BEAM,
            DamageType.ENERGY,
            WeaponArc.FORWARD,
            25,
            10000,
            1.0,
            0,
            5,
            0
        );
    }
}
