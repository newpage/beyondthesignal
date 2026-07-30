package com.beyondsignal.game.websocket;

import com.beyondsignal.game.domain.GameSession;
import com.beyondsignal.game.service.GameSessionService;
import com.beyondsignal.game.service.event.SessionEvent;
import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SessionWebSocketGateway implements Handler<ServerWebSocket> {
    private static final Pattern SESSION_PATH = Pattern.compile("^/ws/session/([0-9a-fA-F-]{36})$");

    private final GameSessionService service;
    private final SessionEventHub eventHub;

    public SessionWebSocketGateway(GameSessionService service, SessionEventHub eventHub) {
        this.service = Objects.requireNonNull(service, "service must not be null");
        this.eventHub = Objects.requireNonNull(eventHub, "eventHub must not be null");
    }

    @Override
    public void handle(ServerWebSocket socket) {
        UUID sessionId = sessionId(socket.path());
        if (sessionId == null) {
            socket.close();
            return;
        }

        final GameSession session;
        try {
            session = service.getSession(sessionId);
        } catch (RuntimeException exception) {
            socket.close();
            return;
        }

        send(socket, "SESSION_SNAPSHOT", Map.of(
            "sessionId", session.id().toString(),
            "sessionName", session.sessionName(),
            "shipName", session.shipName(),
            "status", session.status().name(),
            "hostPlayerId", session.hostPlayerId().toString()
        ));

        final AutoCloseable subscription = eventHub.subscribe(sessionId, event -> send(socket, event));
        socket.textMessageHandler(message -> handleClientMessage(socket, message));
        socket.closeHandler(ignored -> closeQuietly(subscription));
        socket.exceptionHandler(ignored -> closeQuietly(subscription));
    }

    private static void handleClientMessage(ServerWebSocket socket, String message) {
        try {
            JsonObject request = new JsonObject(message);
            if ("PING".equalsIgnoreCase(request.getString("type"))) {
                send(socket, "PONG", Map.of());
            }
        } catch (RuntimeException ignored) {
            send(socket, "ERROR", Map.of("message", "Invalid WebSocket message"));
        }
    }

    private static UUID sessionId(String path) {
        Matcher matcher = SESSION_PATH.matcher(path);
        if (!matcher.matches()) {
            return null;
        }
        try {
            return UUID.fromString(matcher.group(1));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private static void send(ServerWebSocket socket, SessionEvent event) {
        JsonObject message = new JsonObject()
            .put("type", event.type().name())
            .put("sessionId", event.sessionId().toString())
            .put("occurredAt", event.occurredAt().toString())
            .put("payload", new JsonObject(event.payload()));
        socket.writeTextMessage(message.encode());
    }

    private static void send(ServerWebSocket socket, String type, Map<String, Object> payload) {
        socket.writeTextMessage(new JsonObject()
            .put("type", type)
            .put("payload", new JsonObject(payload))
            .encode());
    }

    private static void closeQuietly(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception ignored) {
            // Nothing else to release.
        }
    }
}
