package com.beyondsignal.game.websocket;

import com.beyondsignal.game.service.event.SessionEvent;
import com.beyondsignal.game.service.event.SessionEventPublisher;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class SessionEventHub implements SessionEventPublisher {
    private final ConcurrentHashMap<UUID, CopyOnWriteArrayList<Consumer<SessionEvent>>> subscribers =
        new ConcurrentHashMap<>();

    public AutoCloseable subscribe(UUID sessionId, Consumer<SessionEvent> subscriber) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        Objects.requireNonNull(subscriber, "subscriber must not be null");

        subscribers.computeIfAbsent(sessionId, ignored -> new CopyOnWriteArrayList<>()).add(subscriber);
        return () -> unsubscribe(sessionId, subscriber);
    }

    @Override
    public void publish(SessionEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        var sessionSubscribers = subscribers.get(event.sessionId());
        if (sessionSubscribers != null) {
            sessionSubscribers.forEach(subscriber -> subscriber.accept(event));
        }
    }

    public int subscriberCount(UUID sessionId) {
        var sessionSubscribers = subscribers.get(sessionId);
        return sessionSubscribers == null ? 0 : sessionSubscribers.size();
    }

    private void unsubscribe(UUID sessionId, Consumer<SessionEvent> subscriber) {
        var sessionSubscribers = subscribers.get(sessionId);
        if (sessionSubscribers == null) {
            return;
        }
        sessionSubscribers.remove(subscriber);
        if (sessionSubscribers.isEmpty()) {
            subscribers.remove(sessionId, sessionSubscribers);
        }
    }
}
