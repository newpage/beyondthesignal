package com.beyondsignal.game.simulation.runtime;

import com.beyondsignal.game.simulation.SetThrottleCommand;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SimulationDiagnosticsTest {
    @Test
    void reportsQueueDepthAndTickMetrics() {
        SimulationRuntime runtime = new SimulationRuntime(Duration.ofMillis(50));
        UUID sessionId = UUID.randomUUID();
        runtime.start(sessionId);
        runtime.submit(sessionId, new SetThrottleCommand(50));

        assertThat(runtime.findDiagnostics(sessionId).orElseThrow().commandQueueDepth()).isEqualTo(1);

        runtime.tick(sessionId);
        SimulationDiagnostics diagnostics = runtime.findDiagnostics(sessionId).orElseThrow();

        assertThat(diagnostics.commandQueueDepth()).isZero();
        assertThat(diagnostics.configuredTickRateHz()).isEqualTo(20.0);
        assertThat(diagnostics.lastTickDurationMillis()).isGreaterThanOrEqualTo(0.0);
    }
}
