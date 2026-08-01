package com.beyondsignal.game.combat.ai.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import com.beyondsignal.game.combat.ai.personality.AiPersonality;
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

class ExplainableCombatAiControllerIntegrationTest {
    @Test
    void recordsEveryDecisionInTimeline() {
        CombatSimulationEngine engine = new CombatSimulationEngine();
        CombatId combatId = CombatId.random();
        CombatEncounter encounter = engine.createEncounter(combatId, 44L);

        UUID aiId = UUID.randomUUID();
        UUID enemyId = UUID.randomUUID();

        encounter.addParticipant(new CombatParticipant(
            aiId,
            CombatSide.HOSTILE,
            ShieldModel.uniform(50, 1),
            List.of(WeaponMountState.ready(
                UUID.randomUUID(),
                weapon(),
                0
            )),
            100,
            1.0,
            0.1
        ));
        encounter.addParticipant(new CombatParticipant(
            enemyId,
            CombatSide.FRIENDLY,
            ShieldModel.uniform(50, 1),
            List.of(),
            100,
            0.7,
            0.1
        ));
        encounter.start();

        ExplainableCombatAiController controller =
            new ExplainableCombatAiController(
                aiId,
                AiPersonality.AGGRESSIVE
            );

        ExplainedCombatAiDecision first = controller.decide(encounter, 1000.0);
        controller.submit(encounter, first);
        engine.tick(combatId);

        ExplainedCombatAiDecision second = controller.decide(encounter, 1000.0);

        assertEquals(2, controller.timeline().decisions().size());
        assertFalse(first.explanation().reasons().isEmpty());
        assertFalse(second.explanation().commandTypes().isEmpty());
    }

    private static WeaponDefinition weapon() {
        return new WeaponDefinition(
            "explainable-ai-phaser",
            "Explainable AI Phaser",
            WeaponType.BEAM,
            DamageType.ENERGY,
            WeaponArc.FORWARD,
            30,
            10000,
            1.0,
            0,
            3,
            0
        );
    }
}
