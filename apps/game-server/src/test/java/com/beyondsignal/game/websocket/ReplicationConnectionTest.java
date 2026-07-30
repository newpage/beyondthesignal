package com.beyondsignal.game.websocket;

import com.beyondsignal.game.service.event.SessionEvent;
import com.beyondsignal.game.service.event.SessionEventType;
import com.beyondsignal.game.simulation.ShipState;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReplicationConnectionTest {
    @Test
    void dropsDuplicateAndOutOfOrderShipUpdates() {
        UUID sessionId = UUID.randomUUID();
        List<String> outbound = new ArrayList<>();
        ReplicationConnection connection =
            new ReplicationConnection(outbound::add, new ReplicationMessageSerializer());

        connection.accept(event(sessionId, 8));
        connection.accept(event(sessionId, 7));
        connection.accept(event(sessionId, 8));
        connection.accept(event(sessionId, 9));

        assertThat(outbound).hasSize(2);
        assertThat(new JsonObject(outbound.get(0)).getLong("tick")).isEqualTo(8L);
        assertThat(new JsonObject(outbound.get(1)).getLong("tick")).isEqualTo(9L);
        assertThat(connection.highestShipTick()).isEqualTo(9L);
    }

    @Test
    void reconnectSnapshotCannotOverwriteNewerStreamedState() {
        UUID sessionId = UUID.randomUUID();
        List<String> outbound = new ArrayList<>();
        ReplicationConnection connection =
            new ReplicationConnection(outbound::add, new ReplicationMessageSerializer());

        connection.accept(event(sessionId, 5));
        connection.sendSnapshot(ShipState.initial(sessionId));

        assertThat(outbound).hasSize(1);
        assertThat(new JsonObject(outbound.getFirst()).getLong("tick")).isEqualTo(5L);
    }

    private static SessionEvent event(UUID sessionId, long tick) {
        return new SessionEvent(
            sessionId,
            SessionEventType.SHIP_STATE_UPDATED,
            Instant.EPOCH,
            Map.of(
                "tick", tick,
                "position", Map.of("x", 0.0, "y", 0.0, "z", 0.0),
                "velocity", Map.of("x", 0.0, "y", 0.0, "z", 0.0),
                "headingDegrees", 0.0,
                "throttle", 0,
                "shieldsRaised", false,
                "weaponCooldownTicks", 0,
                "shotsFired", 0L,
                "combatContacts", java.util.List.of()
            )
        );
    }
}
