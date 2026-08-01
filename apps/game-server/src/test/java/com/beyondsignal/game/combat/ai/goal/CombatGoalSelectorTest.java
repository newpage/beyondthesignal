package com.beyondsignal.game.combat.ai.goal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.beyondsignal.game.combat.ai.evaluation.ThreatScore;
import com.beyondsignal.game.combat.ai.evaluation.ThreatTable;
import com.beyondsignal.game.combat.ai.personality.AiPersonality;
import com.beyondsignal.game.combat.ai.personality.AiPersonalityProfile;
import com.beyondsignal.game.combat.ai.planner.DecisionContext;
import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.CombatantSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.ShieldSnapshot;
import com.beyondsignal.game.combat.engine.CombatParticipantStatus;
import com.beyondsignal.game.combat.model.CombatId;
import com.beyondsignal.game.combat.model.CombatSide;
import com.beyondsignal.game.combat.shield.ShieldQuadrant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CombatGoalSelectorTest {
    @Test
    void selectsRetreatForDefensivePersonalityAtLowHull() {
        UUID selfId = UUID.randomUUID();
        CombatantSnapshot self = combatant(selfId, 20, null);
        DecisionContext context = new DecisionContext(
            new CombatAiSnapshot(CombatId.random(), 1L, List.of(self)),
            self,
            new ThreatTable(selfId, List.of())
        );

        CombatAiGoal goal = new CombatGoalSelector().select(
            context,
            AiPersonalityProfile.forPersonality(AiPersonality.DEFENSIVE)
        );

        assertEquals(CombatAiGoalType.RETREAT, goal.type());
    }

    @Test
    void selectsHighestThreatForAcquisition() {
        UUID selfId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        CombatantSnapshot self = combatant(selfId, 100, null);
        DecisionContext context = new DecisionContext(
            new CombatAiSnapshot(CombatId.random(), 1L, List.of(self)),
            self,
            new ThreatTable(selfId, List.of(
                new ThreatScore(targetId, 50, Map.of("score", 50))
            ))
        );

        CombatAiGoal goal = new CombatGoalSelector().select(
            context,
            AiPersonalityProfile.forPersonality(AiPersonality.BALANCED)
        );

        assertEquals(CombatAiGoalType.ACQUIRE_TARGET, goal.type());
        assertEquals(targetId, goal.targetId());
    }

    private static CombatantSnapshot combatant(UUID id, int hull, UUID target) {
        EnumMap<ShieldQuadrant, Integer> strength = new EnumMap<>(ShieldQuadrant.class);
        EnumMap<ShieldQuadrant, Integer> maximum = new EnumMap<>(ShieldQuadrant.class);
        for (ShieldQuadrant quadrant : ShieldQuadrant.values()) {
            strength.put(quadrant, 50);
            maximum.put(quadrant, 50);
        }
        return new CombatantSnapshot(
            id,
            CombatSide.FRIENDLY,
            CombatParticipantStatus.ACTIVE,
            hull,
            100,
            new ShieldSnapshot(strength, maximum),
            List.of(),
            target,
            0.8,
            0.2
        );
    }
}
