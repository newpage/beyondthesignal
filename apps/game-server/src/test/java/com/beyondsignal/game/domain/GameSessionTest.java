package com.beyondsignal.game.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class GameSessionTest {
    private static final Instant CREATED_AT = Instant.parse("2026-07-29T20:00:00Z");

    @Test
    void createsSessionWithConnectedHost() {
        GameSession session = GameSession.create("First Contact", "BTS Horizon", "Robert", CREATED_AT);

        assertThat(session.status()).isEqualTo(SessionStatus.CREATED);
        assertThat(session.players()).hasSize(1);
        assertThat(session.players().getFirst().host()).isTrue();
        assertThat(session.players().getFirst().connected()).isTrue();
        assertThat(session.hostPlayerId()).isEqualTo(session.players().getFirst().id());
        assertThat(session.startedAt()).isEmpty();
    }

    @Test
    void recordsFirstRunningTime() {
        GameSession session = GameSession.create("First Contact", "BTS Horizon", "Robert", CREATED_AT);
        Instant startedAt = CREATED_AT.plusSeconds(30);

        session.transitionTo(SessionStatus.WAITING_FOR_PLAYERS, CREATED_AT.plusSeconds(1));
        session.transitionTo(SessionStatus.READY, CREATED_AT.plusSeconds(2));
        session.transitionTo(SessionStatus.STARTING, CREATED_AT.plusSeconds(3));
        session.transitionTo(SessionStatus.RUNNING, startedAt);
        session.transitionTo(SessionStatus.PAUSED, startedAt.plusSeconds(60));
        session.transitionTo(SessionStatus.RUNNING, startedAt.plusSeconds(120));

        assertThat(session.startedAt()).contains(startedAt);
    }

    @Test
    void rejectsInvalidLifecycleTransition() {
        GameSession session = GameSession.create("First Contact", "BTS Horizon", "Robert", CREATED_AT);

        assertThatThrownBy(() -> session.transitionTo(SessionStatus.RUNNING, CREATED_AT.plusSeconds(1)))
            .isInstanceOf(DomainException.class)
            .hasMessage("Invalid session transition: CREATED -> RUNNING");
    }

    @Test
    void addsPlayerAndAssignsStation() {
        GameSession session = GameSession.create("First Contact", "BTS Horizon", "Robert", CREATED_AT);
        Player helm = Player.crewMember("Maya", CREATED_AT.plusSeconds(5));

        session.addPlayer(helm);
        session.assignStation(BridgeStation.HELM, helm.id(), CREATED_AT.plusSeconds(6));

        assertThat(session.players()).hasSize(2);
        assertThat(session.assignments().get(BridgeStation.HELM).playerId()).isEqualTo(helm.id());
    }

    @Test
    void rejectsDuplicateDisplayNamesIgnoringCase() {
        GameSession session = GameSession.create("First Contact", "BTS Horizon", "Robert", CREATED_AT);

        assertThatThrownBy(() -> session.addPlayer(Player.crewMember("robert", CREATED_AT.plusSeconds(1))))
            .isInstanceOf(DomainException.class)
            .hasMessage("Display name is already in use: robert");
    }

    @Test
    void removesPlayersAndTheirStationAssignments() {
        GameSession session = GameSession.create("First Contact", "BTS Horizon", "Robert", CREATED_AT);
        Player engineer = Player.crewMember("Nia", CREATED_AT.plusSeconds(1));
        session.addPlayer(engineer);
        session.assignStation(BridgeStation.ENGINEERING, engineer.id(), CREATED_AT.plusSeconds(2));

        session.removePlayer(engineer.id());

        assertThat(session.players()).hasSize(1);
        assertThat(session.assignments()).doesNotContainKey(BridgeStation.ENGINEERING);
    }

    @Test
    void preventsHostFromLeaving() {
        GameSession session = GameSession.create("First Contact", "BTS Horizon", "Robert", CREATED_AT);

        assertThatThrownBy(() -> session.removePlayer(session.hostPlayerId()))
            .isInstanceOf(DomainException.class)
            .hasMessage("The host player cannot leave the session");
    }
}
