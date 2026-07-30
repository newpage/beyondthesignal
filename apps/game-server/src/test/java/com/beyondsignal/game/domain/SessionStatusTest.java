package com.beyondsignal.game.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionStatusTest {
    @Test
    void allowsExpectedLifecycleTransitions() {
        assertThat(SessionStatus.CREATED.canTransitionTo(SessionStatus.WAITING_FOR_PLAYERS)).isTrue();
        assertThat(SessionStatus.WAITING_FOR_PLAYERS.canTransitionTo(SessionStatus.READY)).isTrue();
        assertThat(SessionStatus.READY.canTransitionTo(SessionStatus.STARTING)).isTrue();
        assertThat(SessionStatus.STARTING.canTransitionTo(SessionStatus.RUNNING)).isTrue();
        assertThat(SessionStatus.RUNNING.canTransitionTo(SessionStatus.PAUSED)).isTrue();
        assertThat(SessionStatus.PAUSED.canTransitionTo(SessionStatus.RUNNING)).isTrue();
        assertThat(SessionStatus.RUNNING.canTransitionTo(SessionStatus.ENDED)).isTrue();
    }

    @Test
    void preventsRestartingAnEndedSession() {
        assertThat(SessionStatus.ENDED.canTransitionTo(SessionStatus.CREATED)).isFalse();
        assertThat(SessionStatus.ENDED.canTransitionTo(SessionStatus.RUNNING)).isFalse();
    }
}
