package com.beyondsignal.game.sensor;

import com.beyondsignal.game.simulation.TickListener;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public final class SensorController implements TickListener {
    public static final Duration DEFAULT_SCAN_COOLDOWN = Duration.ofSeconds(10);

    private final LongRangeSensorArray sensorArray;
    private SensorState state = SensorState.ready();

    public SensorController(LongRangeSensorArray sensorArray) {
        this.sensorArray = Objects.requireNonNull(sensorArray, "sensorArray");
    }

    public synchronized SensorScanResult scan(
        String originSystemId,
        double rangeLightYears,
        double sensorPower,
        long scanSeed,
        Instant simulationTime
    ) {
        if (!state.readyForScan()) {
            throw new IllegalStateException(
                "Sensors are cooling down for " + state.cooldownRemaining().toMillis() + " ms"
            );
        }

        SensorScanResult result = sensorArray.scan(
            originSystemId,
            rangeLightYears,
            sensorPower,
            scanSeed,
            simulationTime
        );
        state = state.recordScan(result, DEFAULT_SCAN_COOLDOWN);
        return result;
    }

    @Override
    public synchronized void onTick(Instant simulationTime, Duration delta) {
        state = state.tick(delta);
    }

    public synchronized SensorState state() {
        return state;
    }
}
