package com.beyondsignal.game.combat.ai.maneuver.execution;

public enum MovementExecutionState {
    REQUESTED,
    TURNING,
    ACCELERATING,
    CRUISING,
    CORRECTING,
    COMPLETED,
    CANCELLED
}
