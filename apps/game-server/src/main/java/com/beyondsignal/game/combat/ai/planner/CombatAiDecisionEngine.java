package com.beyondsignal.game.combat.ai.planner;

import com.beyondsignal.game.combat.ai.goal.CombatAiGoal;
import com.beyondsignal.game.combat.ai.goal.CombatGoalSelector;
import com.beyondsignal.game.combat.ai.personality.AiPersonality;
import com.beyondsignal.game.combat.ai.personality.AiPersonalityProfile;
import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import java.util.Objects;
import java.util.UUID;

public final class CombatAiDecisionEngine {
    private final DecisionContextFactory contextFactory;
    private final CombatGoalSelector goalSelector;
    private final CombatActionPlanner planner;

    public CombatAiDecisionEngine() {
        this(
            new DecisionContextFactory(),
            new CombatGoalSelector(),
            new CombatActionPlanner()
        );
    }

    public CombatAiDecisionEngine(
        DecisionContextFactory contextFactory,
        CombatGoalSelector goalSelector,
        CombatActionPlanner planner
    ) {
        this.contextFactory = Objects.requireNonNull(contextFactory, "contextFactory");
        this.goalSelector = Objects.requireNonNull(goalSelector, "goalSelector");
        this.planner = Objects.requireNonNull(planner, "planner");
    }

    public CombatAiPlan decide(
        CombatAiSnapshot snapshot,
        UUID selfId,
        AiPersonality personality,
        double engagementDistance
    ) {
        DecisionContext context = contextFactory.create(snapshot, selfId);
        AiPersonalityProfile profile = AiPersonalityProfile.forPersonality(personality);
        CombatAiGoal goal = goalSelector.select(context, profile);
        return planner.plan(context, goal, engagementDistance);
    }
}
