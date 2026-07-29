package com.beyondsignal.game;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import io.vertx.ext.web.client.WebClient;

import java.util.Set;

public final class PlatformVerticle extends AbstractVerticle {
    private final Config config;
    private SqlClient postgres;
    private Redis redis;
    private RedisAPI redisApi;
    private WebClient webClient;

    public PlatformVerticle(Config config) {
        this.config = config;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        PgConnectOptions pgOptions = new PgConnectOptions()
            .setHost(config.postgresHost())
            .setPort(config.postgresPort())
            .setDatabase(config.postgresDatabase())
            .setUser(config.postgresUser())
            .setPassword(config.postgresPassword());

        postgres = io.vertx.pgclient.PgBuilder.pool()
            .with(new PoolOptions().setMaxSize(8))
            .connectingTo(pgOptions)
            .using(vertx)
            .build();

        redis = Redis.createClient(vertx, new RedisOptions()
            .setConnectionString("redis://%s:%d".formatted(config.redisHost(), config.redisPort())));
        redisApi = RedisAPI.api(redis);
        webClient = WebClient.create(vertx);

        Router router = Router.router(vertx);
        router.route().handler(CorsHandler.create()
            .addOrigin("*")
            .allowedMethods(Set.of(io.vertx.core.http.HttpMethod.GET, io.vertx.core.http.HttpMethod.OPTIONS))
            .allowedHeader("Content-Type"));

        router.get("/api/health").handler(ctx -> ctx.json(new JsonObject()
            .put("service", "game-server")
            .put("project", "Beyond the Signal")
            .put("version", "0.1.0")
            .put("status", "UP")));

        router.get("/api/system/status").handler(ctx -> systemStatus()
            .onSuccess(ctx::json)
            .onFailure(error -> ctx.response().setStatusCode(500).end(new JsonObject()
                .put("status", "DOWN")
                .put("error", error.getMessage()).encode())));

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(config.port())
            .onSuccess(server -> {
                System.out.println("HTTP server listening on " + server.actualPort());
                startPromise.complete();
            })
            .onFailure(startPromise::fail);
    }

    private Future<JsonObject> systemStatus() {
        Future<JsonObject> database = postgres.query("SELECT current_database() AS database, version() AS version")
            .execute()
            .map(rows -> {
                var row = rows.iterator().next();
                return component("postgres", "UP")
                    .put("database", row.getString("database"));
            })
            .recover(error -> Future.succeededFuture(component("postgres", "DOWN").put("error", error.getMessage())));

        Future<JsonObject> redisStatus = redisApi.ping(new java.util.ArrayList<>())
            .map(response -> component("redis", "UP").put("response", response.toString()))
            .recover(error -> Future.succeededFuture(component("redis", "DOWN").put("error", error.getMessage())));

        Future<JsonObject> ai = webClient.getAbs(config.aiServiceUrl() + "/health")
            .timeout(3000)
            .send()
            .map(response -> component("ai-service", response.statusCode() == 200 ? "UP" : "DOWN")
                .put("details", response.bodyAsJsonObject()))
            .recover(error -> Future.succeededFuture(component("ai-service", "DOWN").put("error", error.getMessage())));

        return Future.all(database, redisStatus, ai).map(result -> {
            JsonObject components = new JsonObject()
                .put("gameServer", component("game-server", "UP"))
                .put("database", result.resultAt(0))
                .put("redis", result.resultAt(1))
                .put("aiService", result.resultAt(2));

            boolean requiredUp = "UP".equals(components.getJsonObject("database").getString("status"))
                && "UP".equals(components.getJsonObject("redis").getString("status"))
                && "UP".equals(components.getJsonObject("aiService").getString("status"));

            return new JsonObject()
                .put("project", "Beyond the Signal")
                .put("milestone", "1 - Platform Foundation")
                .put("version", "0.1.0")
                .put("status", requiredUp ? "UP" : "DEGRADED")
                .put("components", components);
        });
    }

    private static JsonObject component(String name, String status) {
        return new JsonObject().put("name", name).put("status", status);
    }

    @Override
    public void stop() {
        if (postgres != null) postgres.close();
        if (redis != null) redis.close();
        if (webClient != null) webClient.close();
    }
}
