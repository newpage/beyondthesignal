package com.beyondsignal.game.api;

import com.beyondsignal.game.domain.GameSession;
import com.beyondsignal.game.domain.Player;
import com.beyondsignal.game.domain.StationAssignment;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Comparator;

final class GameSessionJson {
    private GameSessionJson() {
    }

    static JsonObject session(GameSession session) {
        JsonArray players = new JsonArray();
        session.players().stream()
            .sorted(Comparator.comparing(Player::joinedAt))
            .map(GameSessionJson::player)
            .forEach(players::add);

        JsonArray assignments = new JsonArray();
        session.assignments().values().stream()
            .sorted(Comparator.comparing(StationAssignment::station))
            .map(GameSessionJson::assignment)
            .forEach(assignments::add);

        return new JsonObject()
            .put("id", session.id().toString())
            .put("sessionName", session.sessionName())
            .put("shipName", session.shipName())
            .put("status", session.status().name())
            .put("hostPlayerId", session.hostPlayerId().toString())
            .put("createdAt", session.createdAt().toString())
            .put("startedAt", session.startedAt().map(Object::toString).orElse(null))
            .put("players", players)
            .put("stationAssignments", assignments);
    }

    static JsonObject player(Player player) {
        return new JsonObject()
            .put("id", player.id().toString())
            .put("displayName", player.displayName())
            .put("connected", player.connected())
            .put("joinedAt", player.joinedAt().toString())
            .put("host", player.host());
    }

    private static JsonObject assignment(StationAssignment assignment) {
        return new JsonObject()
            .put("station", assignment.station().name())
            .put("playerId", assignment.playerId().toString())
            .put("assignedAt", assignment.assignedAt().toString());
    }
}
