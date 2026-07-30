package com.beyondsignal.game.persistence.jooq;

import org.jooq.Field;
import org.jooq.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.table;

final class GameSessionTables {
    static final Table<?> GAME_SESSIONS = table(name("game_sessions"));
    static final Field<UUID> SESSION_ID = field(name("game_sessions", "id"), UUID.class);
    static final Field<String> SESSION_NAME = field(name("game_sessions", "session_name"), String.class);
    static final Field<String> SHIP_NAME = field(name("game_sessions", "ship_name"), String.class);
    static final Field<String> STATUS = field(name("game_sessions", "status"), String.class);
    static final Field<UUID> HOST_PLAYER_ID = field(name("game_sessions", "host_player_id"), UUID.class);
    static final Field<OffsetDateTime> CREATED_AT = field(name("game_sessions", "created_at"), OffsetDateTime.class);
    static final Field<OffsetDateTime> STARTED_AT = field(name("game_sessions", "started_at"), OffsetDateTime.class);

    static final Table<?> PLAYERS = table(name("players"));
    static final Field<UUID> PLAYER_ID = field(name("players", "id"), UUID.class);
    static final Field<UUID> PLAYER_SESSION_ID = field(name("players", "session_id"), UUID.class);
    static final Field<String> DISPLAY_NAME = field(name("players", "display_name"), String.class);
    static final Field<Boolean> CONNECTED = field(name("players", "connected"), Boolean.class);
    static final Field<OffsetDateTime> JOINED_AT = field(name("players", "joined_at"), OffsetDateTime.class);
    static final Field<Boolean> IS_HOST = field(name("players", "is_host"), Boolean.class);

    static final Table<?> STATION_ASSIGNMENTS = table(name("station_assignments"));
    static final Field<UUID> ASSIGNMENT_SESSION_ID = field(name("station_assignments", "session_id"), UUID.class);
    static final Field<String> STATION = field(name("station_assignments", "station"), String.class);
    static final Field<UUID> ASSIGNMENT_PLAYER_ID = field(name("station_assignments", "player_id"), UUID.class);
    static final Field<OffsetDateTime> ASSIGNED_AT = field(name("station_assignments", "assigned_at"), OffsetDateTime.class);

    private GameSessionTables() {
    }
}
