package com.beyondsignal.game.domain;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum SessionStatus {
    CREATED,
    WAITING_FOR_PLAYERS,
    READY,
    STARTING,
    RUNNING,
    PAUSED,
    ENDED;

    private static final Map<SessionStatus, Set<SessionStatus>> ALLOWED_TRANSITIONS = Map.of(
        CREATED, EnumSet.of(WAITING_FOR_PLAYERS, ENDED),
        WAITING_FOR_PLAYERS, EnumSet.of(READY, ENDED),
        READY, EnumSet.of(WAITING_FOR_PLAYERS, STARTING, ENDED),
        STARTING, EnumSet.of(RUNNING, ENDED),
        RUNNING, EnumSet.of(PAUSED, ENDED),
        PAUSED, EnumSet.of(RUNNING, ENDED),
        ENDED, EnumSet.noneOf(SessionStatus.class)
    );

    public boolean canTransitionTo(SessionStatus target) {
        return target != null && ALLOWED_TRANSITIONS.get(this).contains(target);
    }
}
