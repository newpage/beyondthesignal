package com.beyondsignal.game.combat.ai.explain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class CombatAiDecisionTimeline {
    private final UUID participantId;
    private final List<CombatAiDecisionRecord> decisions = new ArrayList<>();

    public CombatAiDecisionTimeline(UUID participantId) {
        this.participantId = Objects.requireNonNull(participantId, "participantId");
    }

    public synchronized void append(CombatAiDecisionRecord record) {
        Objects.requireNonNull(record, "record");
        if (!record.participantId().equals(participantId)) {
            throw new IllegalArgumentException("Decision belongs to another participant");
        }
        if (!decisions.isEmpty()
            && record.tick() < decisions.getLast().tick()) {
            throw new IllegalArgumentException("Decision ticks must be non-decreasing");
        }
        decisions.add(record);
    }

    public synchronized List<CombatAiDecisionRecord> decisions() {
        return List.copyOf(decisions);
    }

    public synchronized CombatAiDecisionRecord latest() {
        if (decisions.isEmpty()) {
            throw new IllegalStateException("Decision timeline is empty");
        }
        return decisions.getLast();
    }

    public UUID participantId() {
        return participantId;
    }
}
