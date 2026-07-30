package com.beyondsignal.game.simulation.runtime;

import com.beyondsignal.game.service.event.SessionEvent;
import com.beyondsignal.game.service.event.SessionEventPublisher;
import com.beyondsignal.game.service.event.SessionEventType;
import com.beyondsignal.game.simulation.ShipState;

import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class SimulationStateEventPublisher implements SimulationStateListener {
    private final SessionEventPublisher publisher;
    private final Clock clock;

    public SimulationStateEventPublisher(SessionEventPublisher publisher, Clock clock) {
        this.publisher = Objects.requireNonNull(publisher, "publisher must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    @Override
    public void onStateAdvanced(ShipState state) {
        Objects.requireNonNull(state, "state must not be null");

        Map<String, Object> position = new LinkedHashMap<>();
        position.put("x", state.position().x());
        position.put("y", state.position().y());
        position.put("z", state.position().z());

        Map<String, Object> velocity = new LinkedHashMap<>();
        velocity.put("x", state.velocity().x());
        velocity.put("y", state.velocity().y());
        velocity.put("z", state.velocity().z());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("tick", state.tick());
        payload.put("position", Map.copyOf(position));
        payload.put("velocity", Map.copyOf(velocity));
        payload.put("headingDegrees", state.headingDegrees());
        payload.put("throttle", state.throttle());
        payload.put("shieldsRaised", state.shieldsRaised());
        if (state.selectedTargetId() != null) {
            payload.put("selectedTargetId", state.selectedTargetId().toString());
        }

        publisher.publish(new SessionEvent(
            state.sessionId(),
            SessionEventType.SHIP_STATE_UPDATED,
            clock.instant(),
            Map.copyOf(payload)
        ));
    }
}
