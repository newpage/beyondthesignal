package com.beyondsignal.game.websocket;

import com.beyondsignal.game.domain.BridgeStation;
import com.beyondsignal.game.domain.GameSession;
import com.beyondsignal.game.domain.SessionStatus;
import com.beyondsignal.game.service.GameSessionService;
import com.beyondsignal.game.simulation.ClearTargetCommand;
import com.beyondsignal.game.simulation.FireWeaponCommand;
import com.beyondsignal.game.simulation.SelectTargetCommand;
import com.beyondsignal.game.simulation.SetShieldsCommand;
import com.beyondsignal.game.simulation.ShipCommand;
import com.beyondsignal.game.simulation.runtime.ShipCommandGateway;
import com.beyondsignal.game.simulation.runtime.ShipStateProvider;
import io.vertx.core.json.JsonObject;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public final class TacticalCommandHandler {
    private final GameSessionService sessionService;
    private final ShipCommandGateway commandGateway;
    private final ShipStateProvider stateProvider;

    public TacticalCommandHandler(
        GameSessionService sessionService,
        ShipCommandGateway commandGateway,
        ShipStateProvider stateProvider
    ) {
        this.sessionService = Objects.requireNonNull(sessionService, "sessionService must not be null");
        this.commandGateway = Objects.requireNonNull(commandGateway, "commandGateway must not be null");
        this.stateProvider = Objects.requireNonNull(stateProvider, "stateProvider must not be null");
    }

    public void handle(UUID sessionId, JsonObject request, Consumer<JsonObject> response) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        Objects.requireNonNull(request, "request must not be null");
        Objects.requireNonNull(response, "response must not be null");

        String requestId = request.getString("requestId");
        try {
            UUID playerId = requiredUuid(request, "playerId");
            GameSession session = sessionService.getSession(sessionId);
            authorizeTactical(session, playerId);
            ShipCommand command = decode(request);
            if (command instanceof FireWeaponCommand) {
                validateFireState(sessionId);
            }
            commandGateway.submit(sessionId, command);
            response.accept(result("COMMAND_ACCEPTED", requestId)
                .put("command", request.getString("type")));
        } catch (IllegalArgumentException exception) {
            response.accept(error("INVALID_COMMAND", requestId, exception.getMessage()));
        } catch (CommandAuthorizationException exception) {
            response.accept(error("COMMAND_REJECTED", requestId, exception.getMessage()));
        } catch (RuntimeException exception) {
            response.accept(error("COMMAND_REJECTED", requestId, exception.getMessage()));
        }
    }

    private static ShipCommand decode(JsonObject request) {
        String type = requiredText(request, "type");
        return switch (type) {
            case "SET_SHIELDS" -> new SetShieldsCommand(requiredBoolean(request, "raised"));
            case "SELECT_TARGET" -> new SelectTargetCommand(requiredUuid(request, "targetId"));
            case "CLEAR_TARGET" -> new ClearTargetCommand();
            case "FIRE_WEAPON" -> new FireWeaponCommand(requiredText(request, "weapon"));
            default -> throw new IllegalArgumentException("Unsupported tactical command: " + type);
        };
    }


    private void validateFireState(UUID sessionId) {
        var state = stateProvider.findState(sessionId)
            .orElseThrow(() -> new CommandAuthorizationException("Simulation is not running"));
        if (state.selectedTargetId() == null) {
            throw new CommandAuthorizationException("No tactical target is selected");
        }
        if (state.weaponCooldownTicks() > 0) {
            throw new CommandAuthorizationException(
                "Weapons are cooling down for " + state.weaponCooldownTicks() + " more ticks"
            );
        }
    }

    private static void authorizeTactical(GameSession session, UUID playerId) {
        if (session.status() != SessionStatus.RUNNING) {
            throw new CommandAuthorizationException("Session is not running");
        }
        var assignment = session.assignments().get(BridgeStation.TACTICAL);
        if (assignment == null || !assignment.playerId().equals(playerId)) {
            throw new CommandAuthorizationException("Player is not assigned to TACTICAL");
        }
    }

    private static UUID requiredUuid(JsonObject request, String field) {
        String value = requiredText(request, field);
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(field + " must be a UUID");
        }
    }

    private static String requiredText(JsonObject request, String field) {
        String value = request.getString(field);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim();
    }

    private static boolean requiredBoolean(JsonObject request, String field) {
        Boolean value = request.getBoolean(field);
        if (value == null) {
            throw new IllegalArgumentException(field + " must be boolean");
        }
        return value;
    }

    private static JsonObject result(String type, String requestId) {
        JsonObject response = new JsonObject()
            .put("protocolVersion", ReplicationMessageSerializer.PROTOCOL_VERSION)
            .put("type", type);
        if (requestId != null && !requestId.isBlank()) {
            response.put("requestId", requestId);
        }
        return response;
    }

    private static JsonObject error(String type, String requestId, String message) {
        return result(type, requestId).put("message", message == null ? "Command rejected" : message);
    }
}
