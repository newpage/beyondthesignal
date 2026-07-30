package com.beyondsignal.game.simulation.runtime;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RuntimeSimulationSessionLifecycleTest {
    @Test
    void startsAndStopsRuntimeStateForSession() {
        SimulationRuntime runtime = new SimulationRuntime(Duration.ofMillis(50));
        RuntimeSimulationSessionLifecycle lifecycle = new RuntimeSimulationSessionLifecycle(runtime);
        UUID sessionId = UUID.randomUUID();

        lifecycle.start(sessionId);

        assertThat(runtime.findState(sessionId)).isPresent();
        assertThat(lifecycle.stop(sessionId)).isTrue();
        assertThat(runtime.findState(sessionId)).isEmpty();
    }
}
