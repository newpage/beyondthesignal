package com.beyondsignal.game.presentation.websocket;

import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import java.util.Objects;

public final class BattleTelemetryWebSocketGateway
    implements Handler<ServerWebSocket> {

    public static final String PATH = "/ws/battle-telemetry";

    private final BattleTelemetryHub hub;

    public BattleTelemetryWebSocketGateway(BattleTelemetryHub hub) {
        this.hub = Objects.requireNonNull(hub, "hub");
    }

    @Override
    public void handle(ServerWebSocket socket) {
        if (!PATH.equals(socket.path())) {
            socket.close();
            return;
        }
        hub.connect(socket);
    }
}
