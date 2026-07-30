package com.beyondsignal.game.simulation.runtime;

import com.beyondsignal.game.simulation.SetHeadingCommand;
import com.beyondsignal.game.simulation.SetThrottleCommand;
import com.beyondsignal.game.simulation.ShipState;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SimulationRuntimeTest {
    @Test
    void startsOneSimulationPerSession() {
        SimulationRuntime runtime = new SimulationRuntime(Duration.ofMillis(50));
        UUID sessionId = UUID.randomUUID();

        ShipState first = runtime.start(sessionId);
        ShipState second = runtime.start(sessionId);

        assertThat(first).isEqualTo(second);
        assertThat(runtime.activeSessionCount()).isEqualTo(1);
    }

    @Test
    void appliesQueuedCommandsOnNextTickOnly() {
        SimulationRuntime runtime = new SimulationRuntime(Duration.ofSeconds(1));
        UUID sessionId = UUID.randomUUID();
        runtime.start(sessionId);

        runtime.submit(sessionId, new SetHeadingCommand(90));
        runtime.submit(sessionId, new SetThrottleCommand(50));

        assertThat(runtime.requireState(sessionId).tick()).isZero();

        ShipState firstTick = runtime.tick(sessionId);
        ShipState secondTick = runtime.tick(sessionId);

        assertThat(firstTick.tick()).isEqualTo(1);
        assertThat(firstTick.headingDegrees()).isEqualTo(90);
        assertThat(firstTick.position().y()).isCloseTo(20.0, within(0.000001));
        assertThat(secondTick.tick()).isEqualTo(2);
        assertThat(secondTick.position().y()).isCloseTo(40.0, within(0.000001));
    }

    @Test
    void notifiesListenerAfterStateAdvances() {
        List<ShipState> published = new ArrayList<>();
        SimulationRuntime runtime = new SimulationRuntime(
            new com.beyondsignal.game.simulation.ShipSimulationEngine(),
            Duration.ofMillis(50),
            published::add
        );
        UUID sessionId = UUID.randomUUID();
        runtime.start(sessionId);

        ShipState next = runtime.tick(sessionId);

        assertThat(published).containsExactly(next);
    }

    @Test
    void ticksAllSessionsInStableIdentifierOrder() {
        List<UUID> published = new ArrayList<>();
        SimulationRuntime runtime = new SimulationRuntime(
            new com.beyondsignal.game.simulation.ShipSimulationEngine(),
            Duration.ofMillis(50),
            state -> published.add(state.sessionId())
        );
        UUID later = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
        UUID earlier = UUID.fromString("00000000-0000-0000-0000-000000000001");

        runtime.start(later);
        runtime.start(earlier);
        runtime.tickAll();

        assertThat(published).containsExactly(earlier, later);
    }

    @Test
    void rejectsCommandsForInactiveSession() {
        SimulationRuntime runtime = new SimulationRuntime(Duration.ofMillis(50));
        UUID missing = UUID.randomUUID();

        assertThatThrownBy(() -> runtime.submit(missing, new SetThrottleCommand(10)))
            .isInstanceOf(SimulationSessionNotFoundException.class)
            .hasMessageContaining(missing.toString());
    }

    @Test
    void stopsSimulationAndDiscardsItsState() {
        SimulationRuntime runtime = new SimulationRuntime(Duration.ofMillis(50));
        UUID sessionId = UUID.randomUUID();
        runtime.start(sessionId);

        assertThat(runtime.stop(sessionId)).isTrue();
        assertThat(runtime.findState(sessionId)).isEmpty();
        assertThat(runtime.stop(sessionId)).isFalse();
    }

    private static org.assertj.core.data.Offset<Double> within(double value) {
        return org.assertj.core.data.Offset.offset(value);
    }
}
