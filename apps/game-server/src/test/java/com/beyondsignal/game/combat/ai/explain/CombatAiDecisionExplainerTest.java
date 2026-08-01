package com.beyondsignal.game.combat.ai.explain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.ai.evaluation.ThreatScore;
import com.beyondsignal.game.combat.ai.evaluation.ThreatTable;
import com.beyondsignal.game.combat.ai.goal.CombatAiGoal;
import com.beyondsignal.game.combat.ai.goal.CombatAiGoalType;
import com.beyondsignal.game.combat.ai.planner.CombatAiPlan;
import com.beyondsignal.game.combat.ai.planner.DecisionContext;
import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.CombatantSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.ShieldSnapshot;
import com.beyondsignal.game.combat.command.SelectCombatTargetCommand;
import com.beyondsignal.game.combat.engine.CombatParticipantStatus;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.shield.ShieldQuadrant;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatAiDecisionExplainerTest {
    @Test
    void createsStructuredExplanationWithStableOrdering() {
        UUID selfId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        CombatantSnapshot self = combatant(selfId);

        DecisionContext context = new DecisionContext(
            new CombatAiSnapshot(CombatId.random(), 8L, List.of(self)),
            self,
            new ThreatTable(selfId, List.of(
                new ThreatScore(targetId, 70, Map.of("score", 70))
            ))
        );

        CombatAiGoal goal = new CombatAiGoal(
            CombatAiGoalType.ACQUIRE_TARGET,
            targetId,
            90,
            Map.of("aggression", 20, "threatScore", 70)
        );

        CombatAiPlan plan = new CombatAiPlan(
            goal,
            List.of(new SelectCombatTargetCommand(
                context.snapshot().combatId(),
                selfId,
                targetId,
                Instant.EPOCH
            ))
        );

        CombatAiDecisionRecord record =
            new CombatAiDecisionExplainer().explain(context, plan);

        assertEquals(8L, record.tick());
        assertEquals(CombatAiGoalType.ACQUIRE_TARGET, record.goalType());
        assertEquals("threatScore", record.reasons().getFirst().code());
        assertEquals(
            "SelectCombatTargetCommand",
            record.commandTypes().getFirst()
        );
        assertTrue(record.confidence().percentage() > 50);
    }

    private static CombatantSnapshot combatant(UUID id) {
        EnumMap<ShieldQuadrant, Integer> values = new EnumMap<>(ShieldQuadrant.class);
        for (ShieldQuadrant quadrant : ShieldQuadrant.values()) {
            values.put(quadrant, 50);
        }
        return new CombatantSnapshot(
            id,
            CombatSide.FRIENDLY,
            CombatParticipantStatus.ACTIVE,
            100,
            100,
            new ShieldSnapshot(values, values),
            List.of(),
            null,
            0.8,
            0.2
        );
    }
}
