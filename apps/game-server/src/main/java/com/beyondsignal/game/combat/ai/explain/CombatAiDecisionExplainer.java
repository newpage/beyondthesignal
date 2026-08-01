package com.beyondsignal.game.combat.ai.explain;

import com.beyondsignal.game.combat.ai.goal.CombatAiGoal;
import com.beyondsignal.game.combat.ai.goal.CombatAiGoalType;
import com.beyondsignal.game.combat.ai.planner.CombatAiPlan;
import com.beyondsignal.game.combat.ai.planner.DecisionContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class CombatAiDecisionExplainer {
    public CombatAiDecisionRecord explain(
        DecisionContext context,
        CombatAiPlan plan
    ) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(plan, "plan");

        CombatAiGoal goal = plan.goal();
        List<DecisionReason> reasons = new ArrayList<>();

        goal.reasons().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> reasons.add(new DecisionReason(
                entry.getKey(),
                message(entry.getKey(), entry.getValue()),
                entry.getValue()
            )));

        if (plan.commands().isEmpty()) {
            reasons.add(new DecisionReason(
                "noCommands",
                "No executable command was available for the selected goal.",
                0
            ));
        }

        return new CombatAiDecisionRecord(
            context.self().participantId(),
            context.snapshot().tick(),
            goal.type(),
            goal.targetId(),
            confidence(context, plan),
            reasons.stream()
                .sorted(Comparator
                    .comparingInt(DecisionReason::weight)
                    .reversed()
                    .thenComparing(DecisionReason::code))
                .toList(),
            CombatAiDecisionRecord.commandTypes(plan.commands())
        );
    }

    private DecisionConfidence confidence(
        DecisionContext context,
        CombatAiPlan plan
    ) {
        double confidence = switch (plan.goal().type()) {
            case RETREAT -> retreatConfidence(context);
            case ACQUIRE_TARGET, ATTACK_TARGET -> attackConfidence(context, plan);
            case HOLD_POSITION -> 0.55;
        };

        if (plan.commands().isEmpty()
            && plan.goal().type() != CombatAiGoalType.HOLD_POSITION
            && plan.goal().type() != CombatAiGoalType.RETREAT) {
            confidence *= 0.6;
        }

        return new DecisionConfidence(Math.max(0.0, Math.min(1.0, confidence)));
    }

    private double retreatConfidence(DecisionContext context) {
        double hullRisk = 1.0 - context.self().hullPercentage();
        double shieldRisk = 1.0 - context.self().shields().percentage();
        return Math.min(1.0, 0.45 + hullRisk * 0.35 + shieldRisk * 0.20);
    }

    private double attackConfidence(
        DecisionContext context,
        CombatAiPlan plan
    ) {
        double threatConfidence = context.threats().highestThreat()
            .map(score -> Math.min(1.0, score.total() / 100.0))
            .orElse(0.25);
        double readiness = context.self().weapons().isEmpty()
            ? 0.0
            : context.self().readyWeaponCount()
                / (double) context.self().weapons().size();
        return Math.min(1.0, 0.35 + threatConfidence * 0.40 + readiness * 0.25);
    }

    private static String message(String code, int value) {
        return switch (code) {
            case "threatScore" -> "Target has threat score " + value + ".";
            case "aggression" -> "Personality aggression contributed " + value + ".";
            case "lowHull" -> "Hull condition triggered retreat pressure of " + value + ".";
            case "lowShields" -> "Shield condition triggered retreat pressure of " + value + ".";
            case "selfPreservation" ->
                "Self-preservation profile contributed " + value + ".";
            case "noHostiles" -> "No hostile combatants were detected.";
            default -> code + " contributed " + value + ".";
        };
    }
}
