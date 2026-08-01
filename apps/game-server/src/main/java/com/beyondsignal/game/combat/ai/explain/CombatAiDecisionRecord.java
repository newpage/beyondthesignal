package com.beyondsignal.game.combat.ai.explain;

import com.beyondsignal.game.combat.ai.goal.CombatAiGoalType;
import com.beyondsignal.game.combat.command.CombatCommand;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record CombatAiDecisionRecord(
    UUID participantId,
    long tick,
    CombatAiGoalType goalType,
    UUID targetId,
    DecisionConfidence confidence,
    List<DecisionReason> reasons,
    List<String> commandTypes
) {
    public CombatAiDecisionRecord {
        participantId = Objects.requireNonNull(participantId, "participantId");
        if (tick < 0) {
            throw new IllegalArgumentException("tick cannot be negative");
        }
        goalType = Objects.requireNonNull(goalType, "goalType");
        confidence = Objects.requireNonNull(confidence, "confidence");
        reasons = List.copyOf(Objects.requireNonNull(reasons, "reasons"));
        commandTypes = List.copyOf(Objects.requireNonNull(commandTypes, "commandTypes"));
    }

    public static List<String> commandTypes(List<CombatCommand> commands) {
        return commands.stream()
            .map(command -> command.getClass().getSimpleName())
            .toList();
    }
}
