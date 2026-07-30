package com.beyondsignal.game.simulation.runtime;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SimulationLoop implements AutoCloseable {
    private final SimulationRuntime runtime;
    private final ScheduledExecutorService executor;
    private final AtomicBoolean running = new AtomicBoolean();

    public SimulationLoop(SimulationRuntime runtime) {
        this(
            runtime,
            Executors.newSingleThreadScheduledExecutor(runnable -> {
                Thread thread = new Thread(runnable, "ship-simulation-loop");
                thread.setDaemon(true);
                return thread;
            })
        );
    }

    SimulationLoop(SimulationRuntime runtime, ScheduledExecutorService executor) {
        this.runtime = Objects.requireNonNull(runtime, "runtime must not be null");
        this.executor = Objects.requireNonNull(executor, "executor must not be null");
    }

    public void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }

        Duration interval = runtime.tickDuration();
        executor.scheduleAtFixedRate(
            this::tickSafely,
            interval.toNanos(),
            interval.toNanos(),
            TimeUnit.NANOSECONDS
        );
    }

    public boolean isRunning() {
        return running.get();
    }

    private void tickSafely() {
        if (!running.get()) {
            return;
        }

        try {
            runtime.tickAll();
        } catch (RuntimeException error) {
            System.err.println("Simulation tick failed: " + error.getMessage());
        }
    }

    @Override
    public void close() {
        running.set(false);
        executor.shutdownNow();
    }
}
