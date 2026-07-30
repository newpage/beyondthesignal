CREATE TABLE game_sessions (
    id UUID PRIMARY KEY,
    session_name VARCHAR(120) NOT NULL,
    ship_name VARCHAR(120) NOT NULL,
    status VARCHAR(32) NOT NULL,
    host_player_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    started_at TIMESTAMPTZ,
    CONSTRAINT ck_game_sessions_status CHECK (
        status IN ('CREATED', 'WAITING_FOR_PLAYERS', 'READY', 'STARTING', 'RUNNING', 'PAUSED', 'ENDED')
    )
);

CREATE TABLE players (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL,
    display_name VARCHAR(80) NOT NULL,
    connected BOOLEAN NOT NULL DEFAULT TRUE,
    joined_at TIMESTAMPTZ NOT NULL,
    is_host BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_players_session
        FOREIGN KEY (session_id) REFERENCES game_sessions(id) ON DELETE CASCADE,
    CONSTRAINT uq_players_session_display_name UNIQUE (session_id, display_name),
    CONSTRAINT uq_players_session_player UNIQUE (session_id, id)
);

ALTER TABLE game_sessions
    ADD CONSTRAINT fk_game_sessions_host_player
    FOREIGN KEY (id, host_player_id)
    REFERENCES players(session_id, id)
    DEFERRABLE INITIALLY DEFERRED;

CREATE TABLE station_assignments (
    session_id UUID NOT NULL,
    station VARCHAR(32) NOT NULL,
    player_id UUID NOT NULL,
    assigned_at TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (session_id, station),
    CONSTRAINT fk_station_assignments_session
        FOREIGN KEY (session_id) REFERENCES game_sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_station_assignments_player
        FOREIGN KEY (session_id, player_id) REFERENCES players(session_id, id) ON DELETE CASCADE,
    CONSTRAINT ck_station_assignments_station CHECK (
        station IN ('CAPTAIN', 'FIRST_OFFICER', 'HELM', 'TACTICAL', 'SCIENCE', 'ENGINEERING', 'MEDICAL', 'COMMUNICATIONS', 'OBSERVER')
    )
);

CREATE UNIQUE INDEX uq_station_assignments_active_player
    ON station_assignments(session_id, player_id)
    WHERE station <> 'OBSERVER';

CREATE INDEX idx_game_sessions_status_created_at
    ON game_sessions(status, created_at DESC);

CREATE INDEX idx_players_session_id
    ON players(session_id);

CREATE INDEX idx_station_assignments_player_id
    ON station_assignments(player_id);
