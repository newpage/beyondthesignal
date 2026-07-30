package com.beyondsignal.game.websocket;

import com.beyondsignal.game.service.event.SessionEvent;
import com.beyondsignal.game.service.event.SessionEventType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SessionEventHubTest {
    @Test
    void broadcastsOnlyToSubscribersOfTheMatchingSession() throws Exception {
        SessionEventHub hub = new SessionEventHub();
        UUID firstSession = UUID.randomUUID();
        UUID secondSession = UUID.randomUUID();
        List<SessionEvent> firstMessages = new ArrayList<>();
        List<SessionEvent> secondMessages = new ArrayList<>();

        try (AutoCloseable first = hub.subscribe(firstSession, firstMessages::add);
             AutoCloseable second = hub.subscribe(secondSession, secondMessages::add)) {
            SessionEvent event = new SessionEvent(
                firstSession,
                SessionEventType.PLAYER_JOINED,
                Instant.parse("2026-07-29T20:00:00Z"),
                Map.of("playerId", UUID.randomUUID().toString())
            );

            hub.publish(event);

            assertThat(firstMessages).containsExactly(event);
            assertThat(secondMessages).isEmpty();
        }
    }

    @Test
    void removesSubscriberWhenSubscriptionCloses() throws Exception {
        SessionEventHub hub = new SessionEventHub();
        UUID sessionId = UUID.randomUUID();
        List<SessionEvent> messages = new ArrayList<>();

        AutoCloseable subscription = hub.subscribe(sessionId, messages::add);
        assertThat(hub.subscriberCount(sessionId)).isEqualTo(1);

        subscription.close();
        hub.publish(new SessionEvent(
            sessionId,
            SessionEventType.SESSION_READY,
            Instant.parse("2026-07-29T20:00:00Z"),
            Map.of()
        ));

        assertThat(hub.subscriberCount(sessionId)).isZero();
        assertThat(messages).isEmpty();
    }
}
