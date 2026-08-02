package com.beyondsignal.game.combat.event;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CombatEventSubscription implements AutoCloseable {
    private final Runnable unsubscribe;
    private final AtomicBoolean closed = new AtomicBoolean();

    CombatEventSubscription(Runnable unsubscribe) {
        this.unsubscribe = Objects.requireNonNull(unsubscribe, "unsubscribe");
    }

    public boolean active() {
        return !closed.get();
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            unsubscribe.run();
        }
    }
}
