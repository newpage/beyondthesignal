package com.beyondsignal.game.api;

import com.beyondsignal.game.persistence.GameSessionRepository;
import com.beyondsignal.game.service.GameSessionService;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
class GameSessionRoutesTest {
    private WebClient client;
    private int port;

    @BeforeEach
    void startServer(Vertx vertx, VertxTestContext testContext) {
        GameSessionRepository repository = new InMemoryGameSessionRepository();
        GameSessionService service = new GameSessionService(
            repository,
            Clock.fixed(Instant.parse("2026-07-29T20:00:00Z"), ZoneOffset.UTC)
        );

        Router router = Router.router(vertx);
        new GameSessionRoutes(service).mount(router);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(0)
            .onSuccess(server -> {
                port = server.actualPort();
                client = WebClient.create(vertx);
                testContext.completeNow();
            })
            .onFailure(testContext::failNow);
    }

    @AfterEach
    void closeClient() {
        if (client != null) {
            client.close();
        }
    }

    @Test
    void createsAndRetrievesSession(VertxTestContext testContext) {
        JsonObject request = new JsonObject()
            .put("sessionName", "First Contact")
            .put("shipName", "BTS Horizon")
            .put("hostDisplayName", "Robert");

        client.post(port, "localhost", "/api/v1/sessions")
            .as(BodyCodec.jsonObject())
            .sendJsonObject(request)
            .compose(created -> {
                assertThat(created.statusCode()).isEqualTo(201);
                String sessionId = created.body().getString("id");
                assertThat(created.body().getString("status")).isEqualTo("WAITING_FOR_PLAYERS");
                return client.get(port, "localhost", "/api/v1/sessions/" + sessionId)
                    .as(BodyCodec.jsonObject())
                    .send();
            })
            .onSuccess(found -> testContext.verify(() -> {
                assertThat(found.statusCode()).isEqualTo(200);
                assertThat(found.body().getString("sessionName")).isEqualTo("First Contact");
                assertThat(found.body().getJsonArray("players")).hasSize(1);
                testContext.completeNow();
            }))
            .onFailure(testContext::failNow);
    }

    @Test
    void rejectsInvalidCreateRequest(VertxTestContext testContext) {
        client.post(port, "localhost", "/api/v1/sessions")
            .as(BodyCodec.jsonObject())
            .sendJsonObject(new JsonObject().put("sessionName", "Missing Fields"))
            .onSuccess(response -> testContext.verify(() -> {
                assertThat(response.statusCode()).isEqualTo(400);
                assertThat(response.body().getString("code")).isEqualTo("INVALID_REQUEST");
                testContext.completeNow();
            }))
            .onFailure(testContext::failNow);
    }

    @Test
    void returnsNotFoundForUnknownSession(VertxTestContext testContext) {
        client.request(HttpMethod.GET, port, "localhost",
                "/api/v1/sessions/4f518f0c-2c1a-4dab-a767-465f5cb535f5")
            .as(BodyCodec.jsonObject())
            .send()
            .onSuccess(response -> testContext.verify(() -> {
                assertThat(response.statusCode()).isEqualTo(404);
                assertThat(response.body().getString("code")).isEqualTo("SESSION_NOT_FOUND");
                testContext.completeNow();
            }))
            .onFailure(testContext::failNow);
    }
}
