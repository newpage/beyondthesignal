package com.beyondsignal.game.combat.ai.runtime;

import com.beyondsignal.game.combat.ai.personality.AiPersonality;
import com.beyondsignal.game.combat.ai.planner.CombatAiDecisionEngine;
import com.beyondsignal.game.combat.ai.planner.CombatAiPlan;
import com.beyondsignal.game.combat.ai.snapshot.CombatSnapshotFactory;
import com.beyondsignal.game.combat.engine.CombatEncounter;
import java.util.Objects;
import java.util.UUID;

public final class CombatAiController {
    private final UUID participantId;
    private final AiPersonality personality;
    private final CombatSnapshotFactory snapshotFactory;
    private final CombatAiDecisionEngine decisionEngine;

    public CombatAiController(UUID participantId, AiPersonality personality) {
        this(
            participantId,
            personality,
            new CombatSnapshotFactory(),
            new CombatAiDecisionEngine()
        );
    }

    public CombatAiController(
        UUID participantId,
        AiPersonality personality,
        CombatSnapshotFactory snapshotFactory,
        CombatAiDecisionEngine decisionEngine
    ) {
        this.participantId = Objects.requireNonNull(participantId, "participantId");
        this.personality = Objects.requireNonNull(personality, "personality");
        this.snapshotFactory = Objects.requireNonNull(snapshotFactory, "snapshotFactory");
        this.decisionEngine = Objects.requireNonNull(decisionEngine, "decisionEngine");
    }

    public CombatAiPlan decide(CombatEncounter encounter, double engagementDistance) {
        return decisionEngine.decide(
            snapshotFactory.create(encounter),
            participantId,
            personality,
            engagementDistance
        );
    }

    public void submitPlan(CombatEncounter encounter, CombatAiPlan plan) {
        plan.commands().forEach(encounter::submit);
    }
}
