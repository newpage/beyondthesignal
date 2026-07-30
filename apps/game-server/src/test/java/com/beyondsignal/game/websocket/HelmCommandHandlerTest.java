package com.beyondsignal.game.websocket;

import com.beyondsignal.game.domain.BridgeStation;
import com.beyondsignal.game.domain.GameSession;
import com.beyondsignal.game.domain.Player;
import com.beyondsignal.game.persistence.GameSessionRepository;
import com.beyondsignal.game.service.GameSessionService;
import com.beyondsignal.game.service.command.AssignStationCommand;
import com.beyondsignal.game.service.command.CreateSessionCommand;
import com.beyondsignal.game.service.command.JoinSessionCommand;
import com.beyondsignal.game.service.command.StartSessionCommand;
import com.beyondsignal.game.simulation.SetHeadingCommand;
import com.beyondsignal.game.simulation.SetThrottleCommand;
import com.beyondsignal.game.simulation.ShipCommand;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HelmCommandHandlerTest {
    private GameSessionService sessionService;
    private RecordingGateway commandGateway;
    private HelmCommandHandler handler;
    private GameSession session;
    private Player helmPlayer;

    @BeforeEach
    void setUp() {
        sessionService = new GameSessionService(
            new InMemoryRepository(),
            Clock.fixed(Instant.parse("2026-07-30T12:00:00Z"), ZoneOffset.UTC)
        );
        commandGateway = new RecordingGateway();
        handler = new HelmCommandHandler(sessionService, commandGateway);

        session = sessionService.createSession(new CreateSessionCommand("Alpha", "Horizon", "Host"));
        helmPlayer = sessionService.joinSession(new JoinSessionCommand(session.id(), "Helm"));
        sessionService.assignStation(new AssignStationCommand(
            session.id(), session.hostPlayerId(), helmPlayer.id(), BridgeStation.HELM
        ));
        sessionService.markReady(session.id(), session.hostPlayerId());
        sessionService.startSession(new StartSessionCommand(session.id(), session.hostPlayerId()));
    }

    @Test
    void acceptsAuthorizedThrottleCommand() {
        List<JsonObject> responses = new ArrayList<>();

        handler.handle(session.id(), new JsonObject()
            .put("type", "SET_THROTTLE")
            .put("requestId", "cmd-1")
            .put("playerId", helmPlayer.id().toString())
            .put("throttle", 65), responses::add);

        assertThat(commandGateway.commands).singleElement().isEqualTo(new SetThrottleCommand(65));
        assertThat(responses).singleElement().satisfies(response -> {
            assertThat(response.getString("type")).isEqualTo("COMMAND_ACCEPTED");
            assertThat(response.getString("requestId")).isEqualTo("cmd-1");
        });
    }

    @Test
    void acceptsAuthorizedHeadingCommand() {
        List<JsonObject> responses = new ArrayList<>();

        handler.handle(session.id(), new JsonObject()
            .put("type", "SET_HEADING")
            .put("playerId", helmPlayer.id().toString())
            .put("headingDegrees", 405.0), responses::add);

        assertThat(commandGateway.commands).singleElement().isEqualTo(new SetHeadingCommand(405.0));
        assertThat(responses.getFirst().getString("type")).isEqualTo("COMMAND_ACCEPTED");
    }

    @Test
    void rejectsPlayerNotAssignedToHelm() {
        Player observer = sessionService.getSession(session.id()).players().stream()
            .filter(player -> player.host())
            .findFirst()
            .orElseThrow();
        List<JsonObject> responses = new ArrayList<>();

        handler.handle(session.id(), new JsonObject()
            .put("type", "SET_THROTTLE")
            .put("playerId", observer.id().toString())
            .put("throttle", 50), responses::add);

        assertThat(commandGateway.commands).isEmpty();
        assertThat(responses.getFirst().getString("type")).isEqualTo("COMMAND_REJECTED");
        assertThat(responses.getFirst().getString("message")).contains("HELM");
    }

    @Test
    void rejectsMalformedCommandWithoutSubmitting() {
        List<JsonObject> responses = new ArrayList<>();

        handler.handle(session.id(), new JsonObject()
            .put("type", "SET_THROTTLE")
            .put("playerId", helmPlayer.id().toString())
            .put("throttle", 150), responses::add);

        assertThat(commandGateway.commands).isEmpty();
        assertThat(responses.getFirst().getString("type")).isEqualTo("INVALID_COMMAND");
    }

    private static final class RecordingGateway implements com.beyondsignal.game.simulation.runtime.ShipCommandGateway {
        private final List<ShipCommand> commands = new ArrayList<>();
        @Override
        public void submit(UUID sessionId, ShipCommand command) {
            commands.add(command);
        }
    }

    private static final class InMemoryRepository implements GameSessionRepository {
        private final Map<UUID, GameSession> sessions = new LinkedHashMap<>();
        @Override public GameSession save(GameSession gameSession) { sessions.put(gameSession.id(), gameSession); return gameSession; }
        @Override public Optional<GameSession> findById(UUID sessionId) { return Optional.ofNullable(sessions.get(sessionId)); }
        @Override public List<GameSession> findAll() { return List.copyOf(sessions.values()); }
        @Override public boolean existsById(UUID sessionId) { return sessions.containsKey(sessionId); }
        @Override public void deleteById(UUID sessionId) { sessions.remove(sessionId); }
    }
}
