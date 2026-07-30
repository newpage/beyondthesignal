package com.beyondsignal.game.websocket;

import com.beyondsignal.game.domain.GameSession;
import com.beyondsignal.game.service.GameSessionService;
import com.beyondsignal.game.simulation.runtime.ShipCommandGateway;
import com.beyondsignal.game.simulation.runtime.ShipStateProvider;
import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SessionWebSocketGateway implements Handler<ServerWebSocket> {
    private static final Pattern SESSION_PATH = Pattern.compile("^/ws/session/([0-9a-fA-F-]{36})$");

    private final GameSessionService service;
    private final SessionEventHub eventHub;
    private final ShipStateProvider stateProvider;
    private final ReplicationMessageSerializer serializer;
    private final HelmCommandHandler helmCommandHandler;
    private final TacticalCommandHandler tacticalCommandHandler;
    private final EngineeringCommandHandler engineeringCommandHandler;
    private final ScienceCommandHandler scienceCommandHandler;
    private final CaptainCommandHandler captainCommandHandler;

    public SessionWebSocketGateway(GameSessionService service, SessionEventHub eventHub) {
        this(service, eventHub, ignored -> Optional.empty(), (sessionId, command) -> {
            throw new IllegalStateException("Ship command gateway is not configured");
        });
    }

    public SessionWebSocketGateway(
        GameSessionService service,
        SessionEventHub eventHub,
        ShipStateProvider stateProvider,
        ShipCommandGateway commandGateway
    ) {
        this(service, eventHub, stateProvider, commandGateway, new ReplicationMessageSerializer());
    }

    SessionWebSocketGateway(
        GameSessionService service,
        SessionEventHub eventHub,
        ShipStateProvider stateProvider,
        ShipCommandGateway commandGateway,
        ReplicationMessageSerializer serializer
    ) {
        this.service = Objects.requireNonNull(service, "service must not be null");
        this.eventHub = Objects.requireNonNull(eventHub, "eventHub must not be null");
        this.stateProvider = Objects.requireNonNull(stateProvider, "stateProvider must not be null");
        this.serializer = Objects.requireNonNull(serializer, "serializer must not be null");
        ShipCommandGateway requiredGateway =
            Objects.requireNonNull(commandGateway, "commandGateway must not be null");
        this.helmCommandHandler = new HelmCommandHandler(service, requiredGateway);
        this.tacticalCommandHandler = new TacticalCommandHandler(service, requiredGateway, stateProvider);
        this.engineeringCommandHandler = new EngineeringCommandHandler(service, requiredGateway, stateProvider);
        this.scienceCommandHandler = new ScienceCommandHandler(service, requiredGateway, stateProvider);
        this.captainCommandHandler = new CaptainCommandHandler(service, requiredGateway);
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

        ReplicationConnection connection =
            new ReplicationConnection(socket::writeTextMessage, serializer);

        // Subscribe before snapshots so no simulation tick is lost during connection setup.
        final AutoCloseable subscription = eventHub.subscribe(sessionId, connection);

        send(socket, "SESSION_SNAPSHOT", Map.of(
            "sessionId", session.id().toString(),
            "sessionName", session.sessionName(),
            "shipName", session.shipName(),
            "status", session.status().name(),
            "hostPlayerId", session.hostPlayerId().toString()
        ));
        stateProvider.findState(sessionId).ifPresent(connection::sendSnapshot);

        socket.textMessageHandler(message -> handleClientMessage(socket, sessionId, message));
        socket.closeHandler(ignored -> closeQuietly(subscription));
        socket.exceptionHandler(ignored -> closeQuietly(subscription));
    }

    private void handleClientMessage(ServerWebSocket socket, UUID sessionId, String message) {
        try {
            JsonObject request = new JsonObject(message);
            if ("PING".equalsIgnoreCase(request.getString("type"))) {
                send(socket, "PONG", Map.of());
                return;
            }
            String type = request.getString("type", "");
            var response = (java.util.function.Consumer<JsonObject>)
                result -> socket.writeTextMessage(result.encode());
            switch (type) {
                case "SET_THROTTLE", "SET_HEADING" ->
                    helmCommandHandler.handle(sessionId, request, response);
                case "SET_SHIELDS", "SELECT_TARGET", "CLEAR_TARGET", "FIRE_WEAPON" ->
                    tacticalCommandHandler.handle(sessionId, request, response);
                case "ALLOCATE_POWER" -> engineeringCommandHandler.handle(sessionId, request, response);
                case "SCAN_CONTACTS" -> scienceCommandHandler.handle(sessionId, request, response);
                case "SET_RED_ALERT" -> captainCommandHandler.handle(sessionId, request, response);
                default -> socket.writeTextMessage(new JsonObject()
                    .put("protocolVersion", ReplicationMessageSerializer.PROTOCOL_VERSION)
                    .put("type", "INVALID_COMMAND")
                    .put("requestId", request.getString("requestId"))
                    .put("message", "Unsupported bridge command: " + type)
                    .encode());
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

    private static void send(ServerWebSocket socket, String type, Map<String, Object> payload) {
        socket.writeTextMessage(new JsonObject()
            .put("protocolVersion", ReplicationMessageSerializer.PROTOCOL_VERSION)
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
