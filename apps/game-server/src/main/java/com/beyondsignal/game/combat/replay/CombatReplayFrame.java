package com.beyondsignal.game.combat.replay;

import com.beyondsignal.game.combat.event.CombatEvent;
import java.util.List;

public record CombatReplayFrame(long tick, List<CombatEvent> events) {
    public CombatReplayFrame {
        if (tick < 0) {
            throw new IllegalArgumentException("tick cannot be negative");
        }
        events = List.copyOf(events);
    }
}
