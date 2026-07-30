package com.beyondsignal.game.debug;

import com.beyondsignal.game.domain.BridgeStation;
import com.beyondsignal.game.domain.GameSession;
import com.beyondsignal.game.domain.SessionStatus;
import com.beyondsignal.game.simulation.ShipState;
import com.beyondsignal.game.simulation.runtime.SimulationDiagnostics;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class BridgeDebugSnapshotFactoryTest {
    @Test
    void exposesAuthoritativeStateStationsAndRuntimeMetrics() {
        Instant now = Instant.parse("2026-07-30T00:00:00Z");
        GameSession session = GameSession.create("Alpha", "Odyssey", "Robert", now);
        session.transitionTo(SessionStatus.WAITING_FOR_PLAYERS, now);
        session.assignStation(BridgeStation.HELM, session.hostPlayerId(), now);
        session.transitionTo(SessionStatus.READY, now);
        session.transitionTo(SessionStatus.STARTING, now);
        session.transitionTo(SessionStatus.RUNNING, now);

        SimulationDiagnostics diagnostics = new SimulationDiagnostics(
            ShipState.initial(session.id()), 3, 20.0, 1.25, 1.5
        );

        JsonObject json = new BridgeDebugSnapshotFactory().create(session, diagnostics, 2);

        assertThat(json.getJsonObject("session").getString("shipName")).isEqualTo("Odyssey");
        assertThat(json.getJsonObject("ship").getLong("tick")).isZero();
        assertThat(json.getJsonObject("runtime").getInteger("commandQueueDepth")).isEqualTo(3);
        assertThat(json.getJsonObject("runtime").getInteger("websocketSubscribers")).isEqualTo(2);
        assertThat(json.getJsonArray("stations").stream()
            .map(JsonObject.class::cast)
            .filter(station -> "HELM".equals(station.getString("station")))
            .findFirst().orElseThrow().getString("displayName")).isEqualTo("Robert");
    }
}
