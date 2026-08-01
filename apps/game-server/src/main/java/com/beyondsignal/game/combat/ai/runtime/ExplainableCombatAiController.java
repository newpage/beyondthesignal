package com.beyondsignal.game.combat.ai.runtime;

import com.beyondsignal.game.combat.ai.explain.CombatAiDecisionExplainer;
import com.beyondsignal.game.combat.ai.explain.CombatAiDecisionTimeline;
import com.beyondsignal.game.combat.ai.personality.AiPersonality;
import com.beyondsignal.game.combat.ai.planner.CombatAiDecisionEngine;
import com.beyondsignal.game.combat.ai.planner.DecisionContext;
import com.beyondsignal.game.combat.ai.planner.DecisionContextFactory;
import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.CombatSnapshotFactory;
import com.beyondsignal.game.combat.engine.CombatEncounter;
import java.util.Objects;
import java.util.UUID;

public final class ExplainableCombatAiController {
    private final UUID participantId;
    private final AiPersonality personality;
    private final CombatSnapshotFactory snapshotFactory;
    private final DecisionContextFactory contextFactory;
    private final CombatAiDecisionEngine decisionEngine;
    private final CombatAiDecisionExplainer explainer;
    private final CombatAiDecisionTimeline timeline;

    public ExplainableCombatAiController(
        UUID participantId,
        AiPersonality personality
    ) {
        this(
            participantId,
            personality,
            new CombatSnapshotFactory(),
            new DecisionContextFactory(),
            new CombatAiDecisionEngine(),
            new CombatAiDecisionExplainer()
        );
    }

    public ExplainableCombatAiController(
        UUID participantId,
        AiPersonality personality,
        CombatSnapshotFactory snapshotFactory,
        DecisionContextFactory contextFactory,
        CombatAiDecisionEngine decisionEngine,
        CombatAiDecisionExplainer explainer
    ) {
        this.participantId = Objects.requireNonNull(participantId, "participantId");
        this.personality = Objects.requireNonNull(personality, "personality");
        this.snapshotFactory = Objects.requireNonNull(snapshotFactory, "snapshotFactory");
        this.contextFactory = Objects.requireNonNull(contextFactory, "contextFactory");
        this.decisionEngine = Objects.requireNonNull(decisionEngine, "decisionEngine");
        this.explainer = Objects.requireNonNull(explainer, "explainer");
        this.timeline = new CombatAiDecisionTimeline(participantId);
    }

    public ExplainedCombatAiDecision decide(
        CombatEncounter encounter,
        double engagementDistance
    ) {
        CombatAiSnapshot snapshot = snapshotFactory.create(encounter);
        DecisionContext context = contextFactory.create(snapshot, participantId);
        var plan = decisionEngine.decide(
            snapshot,
            participantId,
            personality,
            engagementDistance
        );
        var explanation = explainer.explain(context, plan);
        timeline.append(explanation);
        return new ExplainedCombatAiDecision(plan, explanation);
    }

    public void submit(
        CombatEncounter encounter,
        ExplainedCombatAiDecision decision
    ) {
        decision.plan().commands().forEach(encounter::submit);
    }

    public CombatAiDecisionTimeline timeline() {
        return timeline;
    }
}
