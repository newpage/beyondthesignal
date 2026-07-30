package com.beyondsignal.game.service.event;

@FunctionalInterface
public interface SessionEventPublisher {
    void publish(SessionEvent event);
}
