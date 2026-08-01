package com.beyondsignal.game.combat.ai.maneuver.execution;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record MovementExecutionStep(
    UUID participantId,
    long tick,
    MovementExecutionState state,
    MovementCommand command,
    double progress,
    Map<String, Integer> reasons
) {
    public MovementExecutionStep {
        participantId = Objects.requireNonNull(participantId, "participantId");
        if (tick < 0) {
            throw new IllegalArgumentException("tick cannot be negative");
        }
        state = Objects.requireNonNull(state, "state");
        command = Objects.requireNonNull(command, "command");
        if (!Double.isFinite(progress) || progress < 0.0 || progress > 1.0) {
            throw new IllegalArgumentException("progress must be between 0 and 1");
        }
        reasons = Map.copyOf(Objects.requireNonNull(reasons, "reasons"));
    }
}
