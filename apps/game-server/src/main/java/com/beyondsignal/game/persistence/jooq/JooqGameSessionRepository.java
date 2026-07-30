package com.beyondsignal.game.persistence.jooq;

import com.beyondsignal.game.domain.GameSession;
import com.beyondsignal.game.domain.Player;
import com.beyondsignal.game.domain.StationAssignment;
import com.beyondsignal.game.persistence.GameSessionRepository;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.beyondsignal.game.persistence.jooq.GameSessionRecordMapper.toOffsetDateTime;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.ASSIGNED_AT;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.ASSIGNMENT_PLAYER_ID;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.ASSIGNMENT_SESSION_ID;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.CONNECTED;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.CREATED_AT;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.DISPLAY_NAME;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.GAME_SESSIONS;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.HOST_PLAYER_ID;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.IS_HOST;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.JOINED_AT;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.PLAYER_ID;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.PLAYER_SESSION_ID;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.PLAYERS;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.SESSION_ID;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.SESSION_NAME;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.SHIP_NAME;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.STARTED_AT;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.STATION;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.STATION_ASSIGNMENTS;
import static com.beyondsignal.game.persistence.jooq.GameSessionTables.STATUS;

public final class JooqGameSessionRepository implements GameSessionRepository {
    private final DSLContext dsl;
    private final GameSessionRecordMapper mapper;

    public JooqGameSessionRepository(DSLContext dsl) {
        this(dsl, new GameSessionRecordMapper());
    }

    JooqGameSessionRepository(DSLContext dsl, GameSessionRecordMapper mapper) {
        this.dsl = Objects.requireNonNull(dsl, "dsl must not be null");
        this.mapper = Objects.requireNonNull(mapper, "mapper must not be null");
    }

    @Override
    public GameSession save(GameSession session) {
        Objects.requireNonNull(session, "session must not be null");
        dsl.transaction(configuration -> saveAggregate(configuration, session));
        return session;
    }

    @Override
    public Optional<GameSession> findById(UUID sessionId) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        return Optional.ofNullable(fetchAggregate(dsl, sessionId));
    }

    @Override
    public List<GameSession> findAll() {
        return dsl.select(SESSION_ID)
            .from(GAME_SESSIONS)
            .orderBy(CREATED_AT.desc(), SESSION_ID.asc())
            .fetch(SESSION_ID)
            .stream()
            .map(this::findById)
            .flatMap(Optional::stream)
            .toList();
    }

    @Override
    public boolean existsById(UUID sessionId) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        return dsl.fetchExists(dsl.selectOne().from(GAME_SESSIONS).where(SESSION_ID.eq(sessionId)));
    }

    @Override
    public void deleteById(UUID sessionId) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        dsl.deleteFrom(GAME_SESSIONS).where(SESSION_ID.eq(sessionId)).execute();
    }

    private void saveAggregate(Configuration configuration, GameSession session) {
        DSLContext tx = DSL.using(configuration);

        tx.insertInto(GAME_SESSIONS)
            .columns(SESSION_ID, SESSION_NAME, SHIP_NAME, STATUS, HOST_PLAYER_ID, CREATED_AT, STARTED_AT)
            .values(
                session.id(),
                session.sessionName(),
                session.shipName(),
                session.status().name(),
                session.hostPlayerId(),
                toOffsetDateTime(session.createdAt()),
                session.startedAt().map(GameSessionRecordMapper::toOffsetDateTime).orElse(null)
            )
            .onConflict(SESSION_ID)
            .doUpdate()
            .set(SESSION_NAME, session.sessionName())
            .set(SHIP_NAME, session.shipName())
            .set(STATUS, session.status().name())
            .set(HOST_PLAYER_ID, session.hostPlayerId())
            .set(CREATED_AT, toOffsetDateTime(session.createdAt()))
            .set(STARTED_AT, session.startedAt().map(GameSessionRecordMapper::toOffsetDateTime).orElse(null))
            .execute();

        tx.deleteFrom(STATION_ASSIGNMENTS).where(ASSIGNMENT_SESSION_ID.eq(session.id())).execute();
        tx.deleteFrom(PLAYERS).where(PLAYER_SESSION_ID.eq(session.id())).execute();

        for (Player player : session.players()) {
            tx.insertInto(PLAYERS)
                .columns(PLAYER_ID, PLAYER_SESSION_ID, DISPLAY_NAME, CONNECTED, JOINED_AT, IS_HOST)
                .values(
                    player.id(),
                    session.id(),
                    player.displayName(),
                    player.connected(),
                    toOffsetDateTime(player.joinedAt()),
                    player.host()
                )
                .execute();
        }

        for (StationAssignment assignment : session.assignments().values()) {
            tx.insertInto(STATION_ASSIGNMENTS)
                .columns(ASSIGNMENT_SESSION_ID, STATION, ASSIGNMENT_PLAYER_ID, ASSIGNED_AT)
                .values(
                    session.id(),
                    assignment.station().name(),
                    assignment.playerId(),
                    toOffsetDateTime(assignment.assignedAt())
                )
                .execute();
        }
    }

    private GameSession fetchAggregate(DSLContext context, UUID sessionId) {
        Record sessionRecord = context.select(
                SESSION_ID,
                SESSION_NAME,
                SHIP_NAME,
                STATUS,
                HOST_PLAYER_ID,
                CREATED_AT,
                STARTED_AT
            )
            .from(GAME_SESSIONS)
            .where(SESSION_ID.eq(sessionId))
            .fetchOne();

        if (sessionRecord == null) {
            return null;
        }

        Result<? extends Record> players = context.select(
                PLAYER_ID,
                DISPLAY_NAME,
                CONNECTED,
                JOINED_AT,
                IS_HOST
            )
            .from(PLAYERS)
            .where(PLAYER_SESSION_ID.eq(sessionId))
            .orderBy(JOINED_AT.asc(), PLAYER_ID.asc())
            .fetch();

        Result<? extends Record> assignments = context.select(
                STATION,
                ASSIGNMENT_PLAYER_ID,
                ASSIGNED_AT
            )
            .from(STATION_ASSIGNMENTS)
            .where(ASSIGNMENT_SESSION_ID.eq(sessionId))
            .orderBy(STATION.asc())
            .fetch();

        return mapper.map(sessionRecord, players, assignments);
    }
}
