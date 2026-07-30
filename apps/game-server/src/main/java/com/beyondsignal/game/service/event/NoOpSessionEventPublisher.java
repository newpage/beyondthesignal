package com.beyondsignal.game.service.event;

public enum NoOpSessionEventPublisher implements SessionEventPublisher {
    INSTANCE;

    @Override
    public void publish(SessionEvent event) {
        // Intentionally empty.
    }
}
