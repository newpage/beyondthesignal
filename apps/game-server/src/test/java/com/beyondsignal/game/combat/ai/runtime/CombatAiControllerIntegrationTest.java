package com.beyondsignal.game.combat.ai.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.ai.personality.AiPersonality;
import com.beyondsignal.game.combat.ai.planner.CombatAiPlan;
import com.beyondsignal.game.combat.engine.CombatEncounter;
import com.beyondsignal.game.combat.engine.CombatParticipant;
import com.beyondsignal.game.combat.engine.CombatSimulationEngine;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.shield.ShieldModel;
import com.beyondsignal.game.combat.weapon.DamageType;
import com.beyondsignal.game.combat.weapon.WeaponArc;
import com.beyondsignal.game.combat.weapon.WeaponDefinition;
import com.beyondsignal.game.combat.weapon.WeaponMountState;
import com.beyondsignal.game.combat.weapon.WeaponType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatAiControllerIntegrationTest {
    @Test
    void aiSelectsTargetThenGeneratesFireCommand() {
        CombatId combatId = CombatId.random();
        CombatSimulationEngine engine = new CombatSimulationEngine();
        CombatEncounter encounter = engine.createEncounter(combatId, 42L);

        UUID aiId = UUID.randomUUID();
        UUID enemyId = UUID.randomUUID();
        UUID mountId = UUID.randomUUID();

        CombatParticipant ai = new CombatParticipant(
            aiId,
            CombatSide.HOSTILE,
            ShieldModel.uniform(50, 1),
            List.of(WeaponMountState.ready(mountId, weapon(), 0)),
            100,
            1.0,
            0.1
        );
        CombatParticipant enemy = new CombatParticipant(
            enemyId,
            CombatSide.FRIENDLY,
            ShieldModel.uniform(50, 1),
            List.of(),
            100,
            0.8,
            0.1
        );

        encounter.addParticipant(ai);
        encounter.addParticipant(enemy);
        encounter.start();

        CombatAiController controller = new CombatAiController(
            aiId,
            AiPersonality.AGGRESSIVE
        );

        CombatAiPlan firstPlan = controller.decide(encounter, 1000.0);
        controller.submitPlan(encounter, firstPlan);
        engine.tick(combatId);

        assertEquals(enemyId, ai.selectedTargetId().orElseThrow());

        CombatAiPlan secondPlan = controller.decide(encounter, 1000.0);
        controller.submitPlan(encounter, secondPlan);
        var result = engine.tick(combatId);

        assertTrue(result.eventsProduced().stream()
            .anyMatch(event -> event.type() ==
                com.beyondsignal.game.combat.event.CombatEventType.WEAPON_FIRED));
    }

    private static WeaponDefinition weapon() {
        return new WeaponDefinition(
            "ai-phaser",
            "AI Phaser",
            WeaponType.BEAM,
            DamageType.ENERGY,
            WeaponArc.FORWARD,
            25,
            10000,
            1.0,
            0,
            3,
            0
        );
    }
}
