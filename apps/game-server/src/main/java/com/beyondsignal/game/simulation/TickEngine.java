package com.beyondsignal.game.simulation;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class TickEngine {
    private final List<TickListener> listeners = new CopyOnWriteArrayList<>();
    private Instant simulationTime;

    public TickEngine(Instant initialTime) {
        simulationTime = Objects.requireNonNull(initialTime, "initialTime");
    }

    public void addListener(TickListener listener) {
        listeners.add(Objects.requireNonNull(listener, "listener"));
    }

    public void removeListener(TickListener listener) {
        listeners.remove(listener);
    }

    public synchronized Instant advance(Duration delta) {
        if (delta == null || delta.isNegative() || delta.isZero()) {
            throw new IllegalArgumentException("delta must be positive");
        }

        simulationTime = simulationTime.plus(delta);
        for (TickListener listener : listeners) {
            listener.onTick(simulationTime, delta);
        }
        return simulationTime;
    }

    public synchronized Instant simulationTime() {
        return simulationTime;
    }

    public int listenerCount() {
        return listeners.size();
    }
}
