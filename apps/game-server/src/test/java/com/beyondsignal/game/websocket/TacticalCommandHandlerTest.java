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
import com.beyondsignal.game.simulation.ClearTargetCommand;
import com.beyondsignal.game.simulation.FireWeaponCommand;
import com.beyondsignal.game.simulation.ShipState;
import com.beyondsignal.game.simulation.SelectTargetCommand;
import com.beyondsignal.game.simulation.SetShieldsCommand;
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

class TacticalCommandHandlerTest {
    private GameSessionService sessionService;
    private RecordingGateway commandGateway;
    private TacticalCommandHandler handler;
    private GameSession session;
    private Player tacticalPlayer;

    @BeforeEach
    void setUp() {
        sessionService = new GameSessionService(
            new InMemoryRepository(),
            Clock.fixed(Instant.parse("2026-07-30T12:00:00Z"), ZoneOffset.UTC)
        );
        commandGateway = new RecordingGateway();

        session = sessionService.createSession(new CreateSessionCommand("Alpha", "Horizon", "Host"));
        tacticalPlayer = sessionService.joinSession(new JoinSessionCommand(session.id(), "Tactical"));
        sessionService.assignStation(new AssignStationCommand(
            session.id(), session.hostPlayerId(), tacticalPlayer.id(), BridgeStation.TACTICAL
        ));
        sessionService.markReady(session.id(), session.hostPlayerId());
        sessionService.startSession(new StartSessionCommand(session.id(), session.hostPlayerId()));
        handler = new TacticalCommandHandler(
            sessionService,
            commandGateway,
            ignored -> Optional.of(ShipState.initial(session.id()))
        );
    }

    @Test
    void acceptsAuthorizedShieldCommand() {
        List<JsonObject> responses = new ArrayList<>();

        handler.handle(session.id(), new JsonObject()
            .put("type", "SET_SHIELDS")
            .put("requestId", "tactical-1")
            .put("playerId", tacticalPlayer.id().toString())
            .put("raised", true), responses::add);

        assertThat(commandGateway.commands).singleElement().isEqualTo(new SetShieldsCommand(true));
        assertThat(responses.getFirst().getString("type")).isEqualTo("COMMAND_ACCEPTED");
        assertThat(responses.getFirst().getString("requestId")).isEqualTo("tactical-1");
    }

    @Test
    void acceptsTargetSelectionAndClearCommands() {
        UUID targetId = UUID.randomUUID();
        List<JsonObject> responses = new ArrayList<>();

        handler.handle(session.id(), new JsonObject()
            .put("type", "SELECT_TARGET")
            .put("playerId", tacticalPlayer.id().toString())
            .put("targetId", targetId.toString()), responses::add);
        handler.handle(session.id(), new JsonObject()
            .put("type", "CLEAR_TARGET")
            .put("playerId", tacticalPlayer.id().toString()), responses::add);

        assertThat(commandGateway.commands).containsExactly(
            new SelectTargetCommand(targetId),
            new ClearTargetCommand()
        );
        assertThat(responses).allSatisfy(response ->
            assertThat(response.getString("type")).isEqualTo("COMMAND_ACCEPTED"));
    }


    @Test
    void acceptsFireWeaponWhenTargetSelectedAndWeaponsReady() {
        UUID targetId = UUID.randomUUID();
        ShipState targeted = new com.beyondsignal.game.simulation.ShipSimulationEngine().tick(
            ShipState.initial(session.id()),
            List.of(new SelectTargetCommand(targetId)),
            java.time.Duration.ofMillis(50)
        );
        handler = new TacticalCommandHandler(sessionService, commandGateway, ignored -> Optional.of(targeted));
        List<JsonObject> responses = new ArrayList<>();

        handler.handle(session.id(), new JsonObject()
            .put("type", "FIRE_WEAPON")
            .put("requestId", "tactical-fire-1")
            .put("playerId", tacticalPlayer.id().toString())
            .put("weapon", "PHASER"), responses::add);

        assertThat(commandGateway.commands).singleElement().isEqualTo(new FireWeaponCommand("PHASER"));
        assertThat(responses.getFirst().getString("type")).isEqualTo("COMMAND_ACCEPTED");
    }

    @Test
    void rejectsFireWeaponWithoutSelectedTarget() {
        List<JsonObject> responses = new ArrayList<>();

        handler.handle(session.id(), new JsonObject()
            .put("type", "FIRE_WEAPON")
            .put("playerId", tacticalPlayer.id().toString())
            .put("weapon", "PHASER"), responses::add);

        assertThat(commandGateway.commands).isEmpty();
        assertThat(responses.getFirst().getString("type")).isEqualTo("COMMAND_REJECTED");
        assertThat(responses.getFirst().getString("message")).contains("target");
    }

    @Test
    void rejectsPlayerNotAssignedToTactical() {
        Player host = sessionService.getSession(session.id()).players().stream()
            .filter(Player::host)
            .findFirst()
            .orElseThrow();
        List<JsonObject> responses = new ArrayList<>();

        handler.handle(session.id(), new JsonObject()
            .put("type", "SET_SHIELDS")
            .put("playerId", host.id().toString())
            .put("raised", true), responses::add);

        assertThat(commandGateway.commands).isEmpty();
        assertThat(responses.getFirst().getString("type")).isEqualTo("COMMAND_REJECTED");
        assertThat(responses.getFirst().getString("message")).contains("TACTICAL");
    }

    @Test
    void rejectsMalformedTargetWithoutSubmitting() {
        List<JsonObject> responses = new ArrayList<>();

        handler.handle(session.id(), new JsonObject()
            .put("type", "SELECT_TARGET")
            .put("playerId", tacticalPlayer.id().toString())
            .put("targetId", "not-a-uuid"), responses::add);

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
