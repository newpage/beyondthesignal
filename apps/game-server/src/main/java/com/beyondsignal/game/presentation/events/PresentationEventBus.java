package com.beyondsignal.game.presentation.events;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class PresentationEventBus {
    private final List<BattleFrameReadyListener> listeners =
        new CopyOnWriteArrayList<>();

    public void subscribe(BattleFrameReadyListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener is required");
        }
        listeners.add(listener);
    }

    public boolean unsubscribe(BattleFrameReadyListener listener) {
        return listeners.remove(listener);
    }

    public void publish(BattleFrameReadyEvent event) {
        listeners.forEach(listener -> listener.onFrameReady(event));
    }

    public int listenerCount() {
        return listeners.size();
    }
}
