package com.beyondsignal.game.api;

import com.beyondsignal.game.presentation.runtime.DeveloperBattleRuntime;
import com.beyondsignal.game.presentation.runtime.DeveloperBattleScenario;
import com.beyondsignal.game.presentation.runtime.DeveloperBattleStatus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.Arrays;
import java.util.Objects;

public final class DeveloperBattleRoutes {
    private static final String BASE_PATH = "/api/battle";
    private final DeveloperBattleRuntime runtime;

    public DeveloperBattleRoutes(DeveloperBattleRuntime runtime) {
        this.runtime = Objects.requireNonNull(runtime, "runtime");
    }

    public void mount(Router router) {
        router.route(BASE_PATH + "*").handler(BodyHandler.create());
        router.get(BASE_PATH).handler(context -> json(context, runtime.status()));
        router.get(BASE_PATH + "/scenarios").handler(this::scenarios);
        router.post(BASE_PATH + "/pause").handler(context -> json(context, runtime.pause()));
        router.post(BASE_PATH + "/resume").handler(context -> json(context, runtime.resume()));
        router.post(BASE_PATH + "/reset").handler(this::reset);
    }

    private void scenarios(RoutingContext context) {
        JsonArray values = new JsonArray();
        Arrays.stream(DeveloperBattleScenario.values())
            .map(value -> new JsonObject()
                .put("id", value.name())
                .put("name", value.displayName())
                .put("description", value.description()))
            .forEach(values::add);
        context.json(new JsonObject().put("scenarios", values));
    }

    private void reset(RoutingContext context) {
        try {
            String requested = context.body().asJsonObject().getString("scenario");
            json(context, runtime.reset(DeveloperBattleScenario.valueOf(requested)));
        } catch (RuntimeException error) {
            context.response().setStatusCode(400).end(new JsonObject()
                .put("code", "INVALID_SCENARIO")
                .put("message", "Choose a supported battle scenario.")
                .encode());
        }
    }

    private static void json(RoutingContext context, DeveloperBattleStatus status) {
        context.json(new JsonObject()
            .put("battleId", status.battleId().toString())
            .put("scenario", status.scenario().name())
            .put("state", status.state())
            .put("tick", status.tick())
            .put("outcome", status.outcome())
            .put("allianceOperational", status.allianceOperational())
            .put("hostileOperational", status.hostileOperational()));
    }
}
