package com.beyondsignal.game.websocket;

import com.beyondsignal.game.domain.BridgeStation;
import com.beyondsignal.game.domain.GameSession;
import com.beyondsignal.game.domain.SessionStatus;
import com.beyondsignal.game.service.GameSessionService;
import com.beyondsignal.game.simulation.SetRedAlertCommand;
import com.beyondsignal.game.simulation.runtime.ShipCommandGateway;
import io.vertx.core.json.JsonObject;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public final class CaptainCommandHandler {
    private final GameSessionService sessions;
    private final ShipCommandGateway commands;

    public CaptainCommandHandler(GameSessionService sessions, ShipCommandGateway commands) {
        this.sessions = Objects.requireNonNull(sessions);
        this.commands = Objects.requireNonNull(commands);
    }

    public void handle(UUID sessionId, JsonObject request, Consumer<JsonObject> response) {
        String requestId = request.getString("requestId");
        try {
            UUID playerId = UUID.fromString(requiredText(request, "playerId"));
            GameSession session = sessions.getSession(sessionId);
            authorize(session, playerId);
            Boolean enabled = request.getBoolean("enabled");
            if (enabled == null) throw new IllegalArgumentException("enabled must be boolean");
            commands.submit(sessionId, new SetRedAlertCommand(enabled));
            response.accept(result("COMMAND_ACCEPTED", requestId).put("command", "SET_RED_ALERT"));
        } catch (IllegalArgumentException exception) {
            response.accept(error("INVALID_COMMAND", requestId, exception.getMessage()));
        } catch (RuntimeException exception) {
            response.accept(error("COMMAND_REJECTED", requestId, exception.getMessage()));
        }
    }

    private static void authorize(GameSession session, UUID playerId) {
        if (session.status() != SessionStatus.RUNNING) throw new CommandAuthorizationException("Session is not running");
        var assignment = session.assignments().get(BridgeStation.CAPTAIN);
        if (assignment == null || !assignment.playerId().equals(playerId)) {
            throw new CommandAuthorizationException("Player is not assigned to CAPTAIN");
        }
    }

    private static String requiredText(JsonObject request, String field) {
        String value = request.getString(field);
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required");
        return value.trim();
    }

    private static JsonObject result(String type, String requestId) {
        JsonObject result = new JsonObject().put("protocolVersion", ReplicationMessageSerializer.PROTOCOL_VERSION).put("type", type);
        if (requestId != null && !requestId.isBlank()) result.put("requestId", requestId);
        return result;
    }

    private static JsonObject error(String type, String requestId, String message) {
        return result(type, requestId).put("message", message == null ? "Command rejected" : message);
    }
}
