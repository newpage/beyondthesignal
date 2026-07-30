package com.beyondsignal.game.debug;

import com.beyondsignal.game.domain.BridgeStation;
import com.beyondsignal.game.domain.GameSession;
import com.beyondsignal.game.domain.Player;
import com.beyondsignal.game.simulation.ShipSubsystem;
import com.beyondsignal.game.simulation.runtime.SimulationDiagnostics;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public final class BridgeDebugSnapshotFactory {
    public JsonObject create(GameSession session, SimulationDiagnostics diagnostics, int websocketSubscribers) {
        Objects.requireNonNull(session, "session must not be null");
        Objects.requireNonNull(diagnostics, "diagnostics must not be null");

        Map<UUID, Player> players = session.players().stream()
            .collect(java.util.stream.Collectors.toMap(Player::id, Function.identity()));

        JsonArray stations = new JsonArray();
        for (BridgeStation station : BridgeStation.values()) {
            var assignment = session.assignments().get(station);
            JsonObject stationJson = new JsonObject().put("station", station.name());
            if (assignment != null) {
                Player player = players.get(assignment.playerId());
                stationJson
                    .put("playerId", assignment.playerId().toString())
                    .put("displayName", player == null ? "Unknown" : player.displayName())
                    .put("connected", player != null && player.connected());
            }
            stations.add(stationJson);
        }

        JsonArray playerArray = new JsonArray();
        session.players().stream()
            .sorted(Comparator.comparing(Player::displayName))
            .map(player -> new JsonObject()
                .put("id", player.id().toString())
                .put("displayName", player.displayName())
                .put("connected", player.connected())
                .put("host", player.host()))
            .forEach(playerArray::add);

        JsonObject subsystems = new JsonObject();
        for (ShipSubsystem subsystem : ShipSubsystem.values()) {
            var state = diagnostics.state().subsystems().get(subsystem);
            subsystems.put(subsystem.name(), new JsonObject()
                .put("health", state.health())
                .put("powerAllocation", state.powerAllocation()));
        }

        var state = diagnostics.state();
        return new JsonObject()
            .put("session", new JsonObject()
                .put("id", session.id().toString())
                .put("sessionName", session.sessionName())
                .put("shipName", session.shipName())
                .put("status", session.status().name()))
            .put("players", playerArray)
            .put("stations", stations)
            .put("ship", new JsonObject()
                .put("tick", state.tick())
                .put("position", vector(state.position().x(), state.position().y(), state.position().z()))
                .put("velocity", vector(state.velocity().x(), state.velocity().y(), state.velocity().z()))
                .put("speed", Math.sqrt(
                    Math.pow(state.velocity().x(), 2) +
                    Math.pow(state.velocity().y(), 2) +
                    Math.pow(state.velocity().z(), 2)))
                .put("headingDegrees", state.headingDegrees())
                .put("throttle", state.throttle())
                .put("subsystems", subsystems))
            .put("runtime", new JsonObject()
                .put("configuredTickRateHz", diagnostics.configuredTickRateHz())
                .put("lastTickDurationMillis", diagnostics.lastTickDurationMillis())
                .put("averageTickDurationMillis", diagnostics.averageTickDurationMillis())
                .put("commandQueueDepth", diagnostics.commandQueueDepth())
                .put("websocketSubscribers", websocketSubscribers));
    }

    private static JsonObject vector(double x, double y, double z) {
        return new JsonObject().put("x", x).put("y", y).put("z", z);
    }
}
