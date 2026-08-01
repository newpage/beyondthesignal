package com.beyondsignal.game.combat.ai.runtime;

import com.beyondsignal.game.combat.ai.explain.CombatAiDecisionRecord;
import com.beyondsignal.game.combat.ai.planner.CombatAiPlan;
import java.util.Objects;

public record ExplainedCombatAiDecision(
    CombatAiPlan plan,
    CombatAiDecisionRecord explanation
) {
    public ExplainedCombatAiDecision {
        plan = Objects.requireNonNull(plan, "plan");
        explanation = Objects.requireNonNull(explanation, "explanation");
    }
}
