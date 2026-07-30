package com.beyondsignal.game.websocket;

import com.beyondsignal.game.domain.BridgeStation;
import com.beyondsignal.game.domain.GameSession;
import com.beyondsignal.game.domain.SessionStatus;
import com.beyondsignal.game.service.GameSessionService;
import com.beyondsignal.game.simulation.AllocatePowerCommand;
import com.beyondsignal.game.simulation.ShipSubsystem;
import com.beyondsignal.game.simulation.runtime.ShipCommandGateway;
import com.beyondsignal.game.simulation.runtime.ShipStateProvider;
import io.vertx.core.json.JsonObject;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public final class EngineeringCommandHandler {
    private final GameSessionService sessions;
    private final ShipCommandGateway commands;
    private final ShipStateProvider states;

    public EngineeringCommandHandler(GameSessionService sessions, ShipCommandGateway commands, ShipStateProvider states) {
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
            ShipSubsystem subsystem = ShipSubsystem.valueOf(requiredText(request, "subsystem").toUpperCase());
            int allocation = requiredInteger(request, "powerAllocation");
            AllocatePowerCommand command = new AllocatePowerCommand(subsystem, allocation);
            var state = states.findState(sessionId)
                .orElseThrow(() -> new CommandAuthorizationException("Simulation is not running"));
            int proposedTotal = state.subsystems().entrySet().stream()
                .mapToInt(entry -> entry.getKey() == subsystem ? allocation : entry.getValue().powerAllocation())
                .sum();
            if (proposedTotal > 100) {
                throw new IllegalArgumentException("total subsystem power allocation must not exceed 100; proposed " + proposedTotal);
            }
            commands.submit(sessionId, command);
            response.accept(result("COMMAND_ACCEPTED", requestId).put("command", "ALLOCATE_POWER"));
        } catch (IllegalArgumentException exception) {
            response.accept(error("INVALID_COMMAND", requestId, exception.getMessage()));
        } catch (RuntimeException exception) {
            response.accept(error("COMMAND_REJECTED", requestId, exception.getMessage()));
        }
    }

    private static void authorize(GameSession session, UUID playerId) {
        if (session.status() != SessionStatus.RUNNING) throw new CommandAuthorizationException("Session is not running");
        var assignment = session.assignments().get(BridgeStation.ENGINEERING);
        if (assignment == null || !assignment.playerId().equals(playerId)) {
            throw new CommandAuthorizationException("Player is not assigned to ENGINEERING");
        }
    }

    private static String requiredText(JsonObject request, String field) {
        String value = request.getString(field);
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required");
        return value.trim();
    }

    private static int requiredInteger(JsonObject request, String field) {
        Number value = request.getNumber(field);
        if (value == null || value.doubleValue() != value.intValue()) throw new IllegalArgumentException(field + " must be an integer");
        return value.intValue();
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
