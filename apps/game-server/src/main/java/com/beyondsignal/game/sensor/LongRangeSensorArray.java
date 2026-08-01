package com.beyondsignal.game.sensor;

import com.beyondsignal.game.galaxy.Galaxy;
import com.beyondsignal.game.galaxy.StarSystem;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class LongRangeSensorArray {
    private final Galaxy galaxy;

    public LongRangeSensorArray(Galaxy galaxy) {
        this.galaxy = Objects.requireNonNull(galaxy, "galaxy");
    }

    public SensorScanResult scan(
        String originSystemId,
        double rangeLightYears,
        double sensorPower,
        long scanSeed,
        Instant completedAt
    ) {
        if (!Double.isFinite(rangeLightYears) || rangeLightYears <= 0.0) {
            throw new IllegalArgumentException("rangeLightYears must be positive");
        }
        if (!Double.isFinite(sensorPower) || sensorPower <= 0.0 || sensorPower > 1.0) {
            throw new IllegalArgumentException("sensorPower must be between 0.0 and 1.0");
        }

        StarSystem origin = galaxy.systemById(originSystemId)
            .orElseThrow(() -> new IllegalArgumentException("Unknown star system: " + originSystemId));

        Random random = new Random(scanSeed);
        List<SensorContact> contacts = galaxy.systems()
            .filter(system -> !system.id().equals(origin.id()))
            .map(system -> toContact(origin, system, rangeLightYears, sensorPower, random))
            .filter(Objects::nonNull)
            .sorted(Comparator.comparingDouble(SensorContact::distanceLightYears))
            .toList();

        return new SensorScanResult(origin.id(), rangeLightYears, completedAt, contacts);
    }

    private SensorContact toContact(
        StarSystem origin,
        StarSystem target,
        double rangeLightYears,
        double sensorPower,
        Random random
    ) {
        double distance = origin.distanceTo(target);
        if (distance > rangeLightYears) {
            return null;
        }

        double attenuation = Math.max(0.0, 1.0 - (distance / rangeLightYears));
        double signalStrength = clamp((attenuation * 0.8) + (sensorPower * 0.2));
        double identificationThreshold = 0.28 + random.nextDouble() * 0.18;
        boolean identified = signalStrength >= identificationThreshold;

        return new SensorContact(
            target.id(),
            identified ? target.name() : "Unknown Contact",
            identified ? SensorContactType.STAR_SYSTEM : SensorContactType.UNKNOWN,
            distance,
            signalStrength,
            target.hostileTerritory(),
            identified
        );
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
