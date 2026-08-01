package com.beyondsignal.game.combat.log;

import com.beyondsignal.game.combat.event.CombatEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CombatLog {
    private final List<CombatEvent> events = new ArrayList<>();

    public synchronized void append(CombatEvent event) {
        Objects.requireNonNull(event, "event");
        long expected = events.size() + 1L;
        if (event.sequence() != expected) {
            throw new IllegalArgumentException(
                "Expected combat event sequence " + expected + " but received " + event.sequence()
            );
        }
        events.add(event);
    }

    public synchronized List<CombatEvent> events() { return List.copyOf(events); }
    public synchronized int size() { return events.size(); }

    public synchronized CombatEvent latest() {
        if (events.isEmpty()) throw new IllegalStateException("Combat log is empty");
        return events.getLast();
    }
}
