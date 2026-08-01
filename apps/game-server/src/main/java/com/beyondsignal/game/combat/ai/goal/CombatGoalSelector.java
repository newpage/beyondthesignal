package com.beyondsignal.game.combat.ai.goal;

import com.beyondsignal.game.combat.ai.evaluation.ThreatScore;
import com.beyondsignal.game.combat.ai.personality.AiPersonalityProfile;
import com.beyondsignal.game.combat.ai.planner.DecisionContext;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class CombatGoalSelector {
    public CombatAiGoal select(
        DecisionContext context,
        AiPersonalityProfile personality
    ) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(personality, "personality");

        double hull = context.self().hullPercentage();
        double shields = context.self().shields().percentage();

        if (hull <= personality.retreatHullThreshold()
            || shields <= personality.retreatShieldThreshold()) {
            Map<String, Integer> reasons = new LinkedHashMap<>();
            reasons.put("lowHull", hull <= personality.retreatHullThreshold() ? 60 : 0);
            reasons.put("lowShields", shields <= personality.retreatShieldThreshold() ? 40 : 0);
            reasons.put("selfPreservation",
                (int) Math.round(personality.selfPreservation() * 25));

            return new CombatAiGoal(
                CombatAiGoalType.RETREAT,
                null,
                reasons.values().stream().mapToInt(Integer::intValue).sum(),
                reasons
            );
        }

        ThreatScore highestThreat = context.threats().highestThreat().orElse(null);
        if (highestThreat == null) {
            return new CombatAiGoal(
                CombatAiGoalType.HOLD_POSITION,
                null,
                1,
                Map.of("noHostiles", 1)
            );
        }

        boolean targetAlreadySelected = context.self().selectedTarget()
            .filter(highestThreat.targetId()::equals)
            .isPresent();

        CombatAiGoalType type = targetAlreadySelected
            ? CombatAiGoalType.ATTACK_TARGET
            : CombatAiGoalType.ACQUIRE_TARGET;

        int aggressionBonus = (int) Math.round(personality.aggression() * 30);
        Map<String, Integer> reasons = Map.of(
            "threatScore", highestThreat.total(),
            "aggression", aggressionBonus
        );

        return new CombatAiGoal(
            type,
            highestThreat.targetId(),
            highestThreat.total() + aggressionBonus,
            reasons
        );
    }
}
