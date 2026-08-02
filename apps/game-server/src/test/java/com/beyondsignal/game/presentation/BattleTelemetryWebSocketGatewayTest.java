package com.beyondsignal.game.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.beyondsignal.game.presentation.websocket.BattleTelemetryWebSocketGateway;
import org.junit.jupiter.api.Test;

class BattleTelemetryWebSocketGatewayTest {
    @Test
    void exposesStableTelemetryPath() {
        assertEquals(
            "/ws/battle-telemetry",
            BattleTelemetryWebSocketGateway.PATH
        );
    }
}
