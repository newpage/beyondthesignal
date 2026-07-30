package com.beyondsignal.game;

import com.beyondsignal.game.api.GameSessionRoutes;
import com.beyondsignal.game.persistence.jooq.JooqGameSessionRepository;
import com.beyondsignal.game.service.GameSessionService;
import com.beyondsignal.game.simulation.ShipSimulationEngine;
import com.beyondsignal.game.simulation.runtime.RuntimeSimulationSessionLifecycle;
import com.beyondsignal.game.simulation.runtime.SimulationLoop;
import com.beyondsignal.game.simulation.runtime.SimulationRuntime;
import com.beyondsignal.game.simulation.runtime.SimulationStateEventPublisher;
import com.beyondsignal.game.websocket.SessionEventHub;
import com.beyondsignal.game.websocket.SessionWebSocketGateway;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.postgresql.ds.PGSimpleDataSource;

import java.time.Clock;
import java.time.Duration;
import java.util.Set;

public final class PlatformVerticle extends AbstractVerticle {
    private final Config config;

    private SqlClient postgres;
    private Redis redis;
    private RedisAPI redisApi;
    private WebClient webClient;
    private HttpServer httpServer;
    private DSLContext dsl;
    private SimulationLoop simulationLoop;

    public PlatformVerticle(Config config) {
        this.config = config;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        configureInfrastructure();

        Clock clock = Clock.systemUTC();
        SessionEventHub eventHub = new SessionEventHub();
        SimulationRuntime simulationRuntime = new SimulationRuntime(
            new ShipSimulationEngine(),
            Duration.ofMillis(50),
            new SimulationStateEventPublisher(eventHub, clock)
        );
        simulationLoop = new SimulationLoop(simulationRuntime);

        GameSessionService gameSessionService = new GameSessionService(
            new JooqGameSessionRepository(dsl),
            clock,
            eventHub,
            new RuntimeSimulationSessionLifecycle(simulationRuntime)
        );

        Router router = Router.router(vertx);
        configureCors(router);
        configurePlatformRoutes(router);
        new GameSessionRoutes(gameSessionService).mount(router);

        SessionWebSocketGateway webSocketGateway =
            new SessionWebSocketGateway(gameSessionService, eventHub, simulationRuntime);

        httpServer = vertx.createHttpServer()
            .requestHandler(router)
            .webSocketHandler(webSocketGateway);

        httpServer.listen(config.port())
            .onSuccess(server -> {
                simulationLoop.start();
                System.out.println("HTTP and WebSocket server listening on " + server.actualPort());
                startPromise.complete();
            })
            .onFailure(startPromise::fail);
    }

    private void configureInfrastructure() {
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

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(config.jdbcUrl());
        dataSource.setUser(config.postgresUser());
        dataSource.setPassword(config.postgresPassword());
        dsl = DSL.using(dataSource, SQLDialect.POSTGRES);

        redis = Redis.createClient(vertx, new RedisOptions()
            .setConnectionString("redis://%s:%d".formatted(config.redisHost(), config.redisPort())));
        redisApi = RedisAPI.api(redis);
        webClient = WebClient.create(vertx);
    }

    private static void configureCors(Router router) {
        router.route().handler(CorsHandler.create()
            .addOrigin("*")
            .allowedMethods(Set.of(
                HttpMethod.GET,
                HttpMethod.POST,
                HttpMethod.PUT,
                HttpMethod.DELETE,
                HttpMethod.OPTIONS
            ))
            .allowedHeaders(Set.of(
                "Content-Type",
                "Authorization"
            )));
    }

    private void configurePlatformRoutes(Router router) {
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
    public void stop(Promise<Void> stopPromise) {
        Future<Void> serverClose = httpServer == null
            ? Future.succeededFuture()
            : httpServer.close();

        serverClose.onComplete(ignored -> {
            if (simulationLoop != null) {
                simulationLoop.close();
            }
            if (postgres != null) {
                postgres.close();
            }
            if (redis != null) {
                redis.close();
            }
            if (webClient != null) {
                webClient.close();
            }
            stopPromise.complete();
        });
    }
}
