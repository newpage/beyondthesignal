package com.beyondsignal.game.combat.ai.maneuver.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MovementExecutionSession {
    private final MovementExecutionEngine engine;
    private final MovementCommitment commitment;
    private final List<MovementExecutionStep> steps = new ArrayList<>();

    public MovementExecutionSession(
        MovementExecutionEngine engine,
        MovementCommitment commitment
    ) {
        this.engine = Objects.requireNonNull(engine, "engine");
        this.commitment = Objects.requireNonNull(commitment, "commitment");
    }

    public synchronized MovementExecutionStep update(
        MovementExecutionContext context
    ) {
        MovementExecutionStep step = engine.execute(context);
        steps.add(step);
        return step;
    }

    public synchronized boolean mayReplan(long tick) {
        return commitment.mayReplan(tick);
    }

    public synchronized boolean mustReplan(long tick) {
        return commitment.mustReplan(tick);
    }

    public synchronized List<MovementExecutionStep> steps() {
        return List.copyOf(steps);
    }
}
