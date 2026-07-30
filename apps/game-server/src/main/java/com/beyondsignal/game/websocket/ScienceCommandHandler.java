package com.beyondsignal.game.websocket;

import com.beyondsignal.game.domain.BridgeStation;
import com.beyondsignal.game.domain.GameSession;
import com.beyondsignal.game.domain.SessionStatus;
import com.beyondsignal.game.service.GameSessionService;
import com.beyondsignal.game.simulation.ScanContactsCommand;
import com.beyondsignal.game.simulation.runtime.ShipCommandGateway;
import com.beyondsignal.game.simulation.runtime.ShipStateProvider;
import io.vertx.core.json.JsonObject;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public final class ScienceCommandHandler {
    private final GameSessionService sessions;
    private final ShipCommandGateway commands;
    private final ShipStateProvider states;

    public ScienceCommandHandler(GameSessionService sessions, ShipCommandGateway commands, ShipStateProvider states) {
        this.sessions = Objects.requireNonNull(sessions);
        this.commands = Objects.requireNonNull(commands);
        this.states = Objects.requireNonNull(states);
    }

    public void handle(UUID sessionId, JsonObject request, Consumer<JsonObject> response) {
        String requestId = request.getString("requestId");
        try {
            UUID playerId = UUID.fromString(requiredText(request, "playerId"));
            GameSession session = sessions.getSession(sessionId);
            authorize(session, playerId);
            var state = states.findState(sessionId)
                .orElseThrow(() -> new CommandAuthorizationException("Simulation is not running"));
            if (state.sensorCooldownTicks() > 0) {
                throw new CommandAuthorizationException("Sensors are recalibrating for " + state.sensorCooldownTicks() + " more ticks");
            }
            commands.submit(sessionId, new ScanContactsCommand());
            response.accept(result("COMMAND_ACCEPTED", requestId).put("command", "SCAN_CONTACTS"));
        } catch (IllegalArgumentException exception) {
            response.accept(error("INVALID_COMMAND", requestId, exception.getMessage()));
        } catch (RuntimeException exception) {
            response.accept(error("COMMAND_REJECTED", requestId, exception.getMessage()));
        }
    }

    private static void authorize(GameSession session, UUID playerId) {
        if (session.status() != SessionStatus.RUNNING) throw new CommandAuthorizationException("Session is not running");
        var assignment = session.assignments().get(BridgeStation.SCIENCE);
        if (assignment == null || !assignment.playerId().equals(playerId)) {
            throw new CommandAuthorizationException("Player is not assigned to SCIENCE");
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
