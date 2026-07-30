package com.beyondsignal.game.simulation.runtime;

import com.beyondsignal.game.service.event.SessionEvent;
import com.beyondsignal.game.service.event.SessionEventType;
import com.beyondsignal.game.simulation.ShipState;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SimulationStateEventPublisherTest {
    @Test
    void publishesAuthoritativeShipStateAsSessionEvent() {
        List<SessionEvent> events = new ArrayList<>();
        Instant now = Instant.parse("2026-07-30T00:00:00Z");
        SimulationStateEventPublisher publisher = new SimulationStateEventPublisher(
            events::add,
            Clock.fixed(now, ZoneOffset.UTC)
        );
        ShipState state = ShipState.initial(UUID.randomUUID());

        publisher.onStateAdvanced(state);

        assertThat(events).singleElement().satisfies(event -> {
            assertThat(event.sessionId()).isEqualTo(state.sessionId());
            assertThat(event.type()).isEqualTo(SessionEventType.SHIP_STATE_UPDATED);
            assertThat(event.occurredAt()).isEqualTo(now);
            assertThat(event.payload()).containsEntry("tick", 0L);
            assertThat(event.payload()).containsEntry("headingDegrees", 0.0);
            assertThat(event.payload()).containsEntry("throttle", 0);
        });
    }
}
