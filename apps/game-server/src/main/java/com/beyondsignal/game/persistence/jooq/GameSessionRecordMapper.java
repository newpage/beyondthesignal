package com.beyondsignal.game.persistence.jooq;

import com.beyondsignal.game.domain.BridgeStation;
import com.beyondsignal.game.domain.GameSession;
import com.beyondsignal.game.domain.Player;
import com.beyondsignal.game.domain.SessionStatus;
import com.beyondsignal.game.domain.StationAssignment;
import org.jooq.Record;
import org.jooq.Result;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.beyondsignal.game.persistence.jooq.GameSessionTables.ASSIGNED_AT;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.ASSIGNMENT_PLAYER_ID;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.CONNECTED;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.CREATED_AT;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.DISPLAY_NAME;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.HOST_PLAYER_ID;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.IS_HOST;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.JOINED_AT;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.PLAYER_ID;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.SESSION_ID;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.SESSION_NAME;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.SHIP_NAME;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.STARTED_AT;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.STATION;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.STATUS;

final class GameSessionRecordMapper {
    GameSession map(Record sessionRecord, Result<? extends Record> playerRecords, Result<? extends Record> assignmentRecords) {
        Objects.requireNonNull(sessionRecord, "sessionRecord must not be null");
        Objects.requireNonNull(playerRecords, "playerRecords must not be null");
        Objects.requireNonNull(assignmentRecords, "assignmentRecords must not be null");

        List<Player> players = playerRecords.map(record -> new Player(
            required(record.get(PLAYER_ID), "player id"),
            required(record.get(DISPLAY_NAME), "display name"),
            Boolean.TRUE.equals(record.get(CONNECTED)),
            required(record.get(JOINED_AT), "joined at").toInstant(),
            Boolean.TRUE.equals(record.get(IS_HOST))
        ));

        Map<BridgeStation, StationAssignment> assignments = new EnumMap<>(BridgeStation.class);
        assignmentRecords.forEach(record -> {
            BridgeStation station = BridgeStation.valueOf(required(record.get(STATION), "station"));
            StationAssignment previous = assignments.put(station, new StationAssignment(
                station,
                required(record.get(ASSIGNMENT_PLAYER_ID), "assignment player id"),
                required(record.get(ASSIGNED_AT), "assigned at").toInstant()
            ));
            if (previous != null) {
                throw new IllegalStateException("Duplicate station assignment: " + station);
            }
        });

        OffsetDateTime startedAt = sessionRecord.get(STARTED_AT);
        return GameSession.restore(
            required(sessionRecord.get(SESSION_ID), "session id"),
            required(sessionRecord.get(SESSION_NAME), "session name"),
            required(sessionRecord.get(SHIP_NAME), "ship name"),
            required(sessionRecord.get(HOST_PLAYER_ID), "host player id"),
            required(sessionRecord.get(CREATED_AT), "created at").toInstant(),
            SessionStatus.valueOf(required(sessionRecord.get(STATUS), "status")),
            startedAt == null ? null : startedAt.toInstant(),
            players,
            assignments
        );
    }

    static OffsetDateTime toOffsetDateTime(java.time.Instant instant) {
        return instant == null ? null : instant.atOffset(ZoneOffset.UTC);
    }

    private static <T> T required(T value, String name) {
        return Objects.requireNonNull(value, name + " must not be null");
    }
}
