package com.beyondsignal.game.api;

import com.beyondsignal.game.domain.BridgeStation;
import com.beyondsignal.game.domain.DomainException;
import com.beyondsignal.game.service.GameSessionService;
import com.beyondsignal.game.service.SessionAuthorizationException;
import com.beyondsignal.game.service.SessionNotFoundException;
import com.beyondsignal.game.service.command.AssignStationCommand;
import com.beyondsignal.game.service.command.CreateSessionCommand;
import com.beyondsignal.game.service.command.JoinSessionCommand;
import com.beyondsignal.game.service.command.LeaveSessionCommand;
import com.beyondsignal.game.service.command.StartSessionCommand;
import com.beyondsignal.game.service.command.UnassignStationCommand;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.Objects;
import java.util.UUID;

public final class GameSessionRoutes {
    private static final String BASE_PATH = "/api/v1/sessions";

    private final GameSessionService service;

    public GameSessionRoutes(GameSessionService service) {
        this.service = Objects.requireNonNull(service, "service must not be null");
    }

    public void mount(Router router) {
        Objects.requireNonNull(router, "router must not be null");

        router.route(BASE_PATH + "*").handler(BodyHandler.create());
        router.post(BASE_PATH).handler(this::createSession);
        router.get(BASE_PATH).handler(this::listSessions);
        router.get(BASE_PATH + "/:sessionId").handler(this::getSession);
        router.post(BASE_PATH + "/:sessionId/join").handler(this::joinSession);
        router.post(BASE_PATH + "/:sessionId/leave").handler(this::leaveSession);
        router.put(BASE_PATH + "/:sessionId/stations/:station").handler(this::assignStation);
        router.delete(BASE_PATH + "/:sessionId/stations/:station").handler(this::unassignStation);
        router.post(BASE_PATH + "/:sessionId/ready").handler(this::markReady);
        router.post(BASE_PATH + "/:sessionId/start").handler(this::startSession);
    }

    private void createSession(RoutingContext context) {
        execute(context, () -> {
            JsonObject body = body(context);
            var session = service.createSession(new CreateSessionCommand(
                requiredText(body, "sessionName"),
                requiredText(body, "shipName"),
                requiredText(body, "hostDisplayName")
            ));
            context.response()
                .setStatusCode(201)
                .putHeader("Location", BASE_PATH + "/" + session.id())
                .end(GameSessionJson.session(session).encode());
        });
    }

    private void listSessions(RoutingContext context) {
        execute(context, () -> {
            JsonArray sessions = new JsonArray();
            service.listSessions().stream()
                .map(GameSessionJson::session)
                .forEach(sessions::add);
            json(context, 200, new JsonObject().put("sessions", sessions));
        });
    }

    private void getSession(RoutingContext context) {
        execute(context, () ->
            json(context, 200, GameSessionJson.session(service.getSession(sessionId(context)))));
    }

    private void joinSession(RoutingContext context) {
        execute(context, () -> {
            JsonObject body = body(context);
            var player = service.joinSession(new JoinSessionCommand(
                sessionId(context),
                requiredText(body, "displayName")
            ));
            json(context, 201, GameSessionJson.player(player));
        });
    }

    private void leaveSession(RoutingContext context) {
        execute(context, () -> {
            JsonObject body = body(context);
            var session = service.leaveSession(new LeaveSessionCommand(
                sessionId(context),
                requiredUuid(body, "playerId")
            ));
            json(context, 200, GameSessionJson.session(session));
        });
    }

    private void assignStation(RoutingContext context) {
        execute(context, () -> {
            JsonObject body = body(context);
            var session = service.assignStation(new AssignStationCommand(
                sessionId(context),
                requiredUuid(body, "requestingPlayerId"),
                requiredUuid(body, "playerId"),
                station(context)
            ));
            json(context, 200, GameSessionJson.session(session));
        });
    }

    private void unassignStation(RoutingContext context) {
        execute(context, () -> {
            JsonObject body = body(context);
            var session = service.unassignStation(new UnassignStationCommand(
                sessionId(context),
                requiredUuid(body, "requestingPlayerId"),
                station(context)
            ));
            json(context, 200, GameSessionJson.session(session));
        });
    }

    private void markReady(RoutingContext context) {
        execute(context, () -> {
            JsonObject body = body(context);
            var session = service.markReady(sessionId(context), requiredUuid(body, "requestingPlayerId"));
            json(context, 200, GameSessionJson.session(session));
        });
    }

    private void startSession(RoutingContext context) {
        execute(context, () -> {
            JsonObject body = body(context);
            var session = service.startSession(new StartSessionCommand(
                sessionId(context),
                requiredUuid(body, "requestingPlayerId")
            ));
            json(context, 200, GameSessionJson.session(session));
        });
    }

    private static JsonObject body(RoutingContext context) {
        if (context.body() == null || context.body().buffer() == null || context.body().buffer().length() == 0) {
            throw new IllegalArgumentException("Request body is required");
        }
        return context.body().asJsonObject();
    }

    private static UUID sessionId(RoutingContext context) {
        return parseUuid(context.pathParam("sessionId"), "sessionId");
    }

    private static BridgeStation station(RoutingContext context) {
        String value = context.pathParam("station");
        try {
            return BridgeStation.valueOf(value.toUpperCase());
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("Unknown bridge station: " + value);
        }
    }

    private static String requiredText(JsonObject body, String field) {
        String value = body.getString(field);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }

    private static UUID requiredUuid(JsonObject body, String field) {
        return parseUuid(requiredText(body, field), field);
    }

    private static UUID parseUuid(String value, String field) {
        try {
            return UUID.fromString(value);
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException(field + " must be a valid UUID");
        }
    }

    private static void execute(RoutingContext context, Runnable action) {
        try {
            action.run();
        } catch (SessionNotFoundException exception) {
            error(context, 404, "SESSION_NOT_FOUND", exception.getMessage());
        } catch (SessionAuthorizationException exception) {
            error(context, 403, "SESSION_FORBIDDEN", exception.getMessage());
        } catch (DomainException exception) {
            error(context, 409, "SESSION_CONFLICT", exception.getMessage());
        } catch (IllegalArgumentException | NullPointerException | DecodeException exception) {
            error(context, 400, "INVALID_REQUEST", exception.getMessage());
        } catch (Exception exception) {
            error(context, 500, "INTERNAL_ERROR", "An unexpected error occurred");
        }
    }

    private static void json(RoutingContext context, int statusCode, JsonObject payload) {
        context.response()
            .setStatusCode(statusCode)
            .putHeader("Content-Type", "application/json")
            .end(payload.encode());
    }

    private static void error(RoutingContext context, int statusCode, String code, String message) {
        json(context, statusCode, JsonObject.mapFrom(new ApiError(code, message)));
    }
}
