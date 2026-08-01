package com.beyondsignal.game.combat.ai.fleet.formation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FormationCombatDecisionEngineTest {
    @Test
    void breaksFormationForCriticalSurvivalState() {
        FormationMemberCombatState member = new FormationMemberCombatState(
            UUID.randomUUID(),
            0.10,
            0.60,
            false,
            false,
            FormationCombatState.MAINTAINING
        );

        FormationCombatDecision decision =
            new FormationCombatDecisionEngine().decide(
                member,
                FormationCombatPolicy.standard(),
                false
            );

        assertEquals(
            FormationCombatState.BROKEN_FOR_SURVIVAL,
            decision.state()
        );
        assertEquals(100, decision.priority());
    }

    @Test
    void rejoinsFormationAfterRecovery() {
        FormationMemberCombatState member = new FormationMemberCombatState(
            UUID.randomUUID(),
            0.60,
            0.50,
            false,
            false,
            FormationCombatState.BROKEN_FOR_SURVIVAL
        );

        FormationCombatDecision decision =
            new FormationCombatDecisionEngine().decide(
                member,
                FormationCombatPolicy.standard(),
                false
            );

        assertEquals(FormationCombatState.REJOINING, decision.state());
    }
}
