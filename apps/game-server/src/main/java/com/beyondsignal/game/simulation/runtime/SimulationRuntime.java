package com.beyondsignal.game.simulation.runtime;

import com.beyondsignal.game.simulation.ShipCommand;
import com.beyondsignal.game.simulation.ShipSimulationEngine;
import com.beyondsignal.game.simulation.ShipState;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class SimulationRuntime {
    private final ShipSimulationEngine engine;
    private final Duration tickDuration;
    private final SimulationStateListener stateListener;
    private final ConcurrentHashMap<UUID, ActiveSimulation> simulations = new ConcurrentHashMap<>();

    public SimulationRuntime(Duration tickDuration) {
        this(new ShipSimulationEngine(), tickDuration, NoOpSimulationStateListener.INSTANCE);
    }

    public SimulationRuntime(
        ShipSimulationEngine engine,
        Duration tickDuration,
        SimulationStateListener stateListener
    ) {
        this.engine = Objects.requireNonNull(engine, "engine must not be null");
        this.tickDuration = requirePositive(tickDuration);
        this.stateListener = Objects.requireNonNull(stateListener, "stateListener must not be null");
    }

    public ShipState start(UUID sessionId) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");

        ActiveSimulation created = new ActiveSimulation(ShipState.initial(sessionId));
        ActiveSimulation existing = simulations.putIfAbsent(sessionId, created);
        return (existing == null ? created : existing).state();
    }

    public Optional<ShipState> findState(UUID sessionId) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        ActiveSimulation simulation = simulations.get(sessionId);
        return simulation == null ? Optional.empty() : Optional.of(simulation.state());
    }

    public ShipState requireState(UUID sessionId) {
        return findState(sessionId)
            .orElseThrow(() -> new SimulationSessionNotFoundException(sessionId));
    }

    public void submit(UUID sessionId, ShipCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        active(sessionId).commands().add(command);
    }

    public ShipState tick(UUID sessionId) {
        ActiveSimulation simulation = active(sessionId);

        synchronized (simulation) {
            List<ShipCommand> commands = drain(simulation.commands());
            ShipState next = engine.tick(simulation.state(), commands, tickDuration);
            simulation.state(next);
            stateListener.onStateAdvanced(next);
            return next;
        }
    }

    public List<ShipState> tickAll() {
        return simulations.keySet().stream()
            .sorted()
            .map(this::tick)
            .toList();
    }

    public boolean stop(UUID sessionId) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        return simulations.remove(sessionId) != null;
    }

    public int activeSessionCount() {
        return simulations.size();
    }

    public Duration tickDuration() {
        return tickDuration;
    }

    private ActiveSimulation active(UUID sessionId) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        ActiveSimulation simulation = simulations.get(sessionId);
        if (simulation == null) {
            throw new SimulationSessionNotFoundException(sessionId);
        }
        return simulation;
    }

    private static List<ShipCommand> drain(ConcurrentLinkedQueue<ShipCommand> queue) {
        List<ShipCommand> commands = new ArrayList<>();
        ShipCommand command;
        while ((command = queue.poll()) != null) {
            commands.add(command);
        }
        return List.copyOf(commands);
    }

    private static Duration requirePositive(Duration duration) {
        Objects.requireNonNull(duration, "tickDuration must not be null");
        if (duration.isZero() || duration.isNegative()) {
            throw new IllegalArgumentException("tickDuration must be positive");
        }
        return duration;
    }

    private static final class ActiveSimulation {
        private volatile ShipState state;
        private final ConcurrentLinkedQueue<ShipCommand> commands = new ConcurrentLinkedQueue<>();

        private ActiveSimulation(ShipState state) {
            this.state = state;
        }

        private ShipState state() {
            return state;
        }

        private void state(ShipState state) {
            this.state = state;
        }

        private ConcurrentLinkedQueue<ShipCommand> commands() {
            return commands;
        }
    }
}
