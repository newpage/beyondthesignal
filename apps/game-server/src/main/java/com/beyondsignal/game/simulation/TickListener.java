package com.beyondsignal.game.simulation;

import java.time.Duration;
import java.time.Instant;

@FunctionalInterface
public interface TickListener {
    void onTick(Instant simulationTime, Duration delta);
}
