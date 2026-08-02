package com.beyondsignal.game.combat.event;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Encounter-independent synchronous event bus.
 *
 * Listeners execute in subscription order on the combat tick thread. A listener
 * should therefore hand off expensive work rather than blocking simulation.
 */
public final class CombatEventBus {
    private final AtomicLong nextSubscriptionId = new AtomicLong(1);
    private final Map<Long, CombatEventListener> listeners = new LinkedHashMap<>();

    public synchronized CombatEventSubscription subscribe(
        CombatEventListener listener
    ) {
        Objects.requireNonNull(listener, "listener");
        long id = nextSubscriptionId.getAndIncrement();
        listeners.put(id, listener);
        return new CombatEventSubscription(() -> unsubscribe(id));
    }

    public void publish(CombatEventBatch batch) {
        Objects.requireNonNull(batch, "batch");
        if (batch.empty()) {
            return;
        }

        CombatEventListener[] snapshot;
        synchronized (this) {
            snapshot = listeners.values().toArray(CombatEventListener[]::new);
        }
        for (CombatEventListener listener : snapshot) {
            listener.onEvents(batch);
        }
    }

    public synchronized int subscriberCount() {
        return listeners.size();
    }

    private synchronized void unsubscribe(long id) {
        listeners.remove(id);
    }
}
