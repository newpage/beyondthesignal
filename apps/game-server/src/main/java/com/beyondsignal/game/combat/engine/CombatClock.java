package com.beyondsignal.game.combat.engine;

/**
 * Deterministic tick-only combat clock.
 */
public final class CombatClock {
    private long tick;

    public CombatClock() {
        this(0L);
    }

    public CombatClock(long startingTick) {
        if (startingTick < 0) {
            throw new IllegalArgumentException("startingTick cannot be negative");
        }
        tick = startingTick;
    }

    public synchronized long currentTick() {
        return tick;
    }

    public synchronized long advance() {
        return ++tick;
    }

    public synchronized void reset() {
        tick = 0L;
    }
}
