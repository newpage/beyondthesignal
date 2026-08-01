package com.beyondsignal.game.bridge;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public final class BridgeEventHub {
    private final Set<BridgeStateListener> listeners = new CopyOnWriteArraySet<>();

    public AutoCloseable subscribe(BridgeStateListener listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    public void publish(BridgeState state) {
        for (BridgeStateListener listener : listeners) {
            listener.onBridgeStateChanged(state);
        }
    }

    public int subscriberCount() {
        return listeners.size();
    }
}
