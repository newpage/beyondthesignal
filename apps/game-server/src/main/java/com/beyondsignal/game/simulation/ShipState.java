package com.beyondsignal.game.simulation;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record ShipState(
    UUID sessionId,
    long tick,
    Vector3 position,
    Vector3 velocity,
    double headingDegrees,
    int throttle,
    boolean shieldsRaised,
    UUID selectedTargetId,
    Map<ShipSubsystem, SubsystemState> subsystems
) {
    public ShipState {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(velocity, "velocity must not be null");

        if (tick < 0) {
            throw new IllegalArgumentException("tick must not be negative");
        }
        if (!Double.isFinite(headingDegrees) || headingDegrees < 0.0 || headingDegrees >= 360.0) {
            throw new IllegalArgumentException("headingDegrees must be between 0 inclusive and 360 exclusive");
        }
        if (throttle < 0 || throttle > 100) {
            throw new IllegalArgumentException("throttle must be between 0 and 100");
        }

        EnumMap<ShipSubsystem, SubsystemState> copy = new EnumMap<>(ShipSubsystem.class);
        copy.putAll(Objects.requireNonNull(subsystems, "subsystems must not be null"));
        for (ShipSubsystem subsystem : ShipSubsystem.values()) {
            if (!copy.containsKey(subsystem)) {
                throw new IllegalArgumentException("missing subsystem state: " + subsystem);
            }
        }
        subsystems = Collections.unmodifiableMap(copy);
    }

    public static ShipState initial(UUID sessionId) {
        EnumMap<ShipSubsystem, SubsystemState> systems = new EnumMap<>(ShipSubsystem.class);
        systems.put(ShipSubsystem.ENGINES, SubsystemState.nominal(40));
        systems.put(ShipSubsystem.SHIELDS, SubsystemState.nominal(20));
        systems.put(ShipSubsystem.SENSORS, SubsystemState.nominal(15));
        systems.put(ShipSubsystem.WEAPONS, SubsystemState.nominal(15));
        systems.put(ShipSubsystem.LIFE_SUPPORT, SubsystemState.nominal(10));

        return new ShipState(sessionId, 0, Vector3.ZERO, Vector3.ZERO, 0.0, 0, false, null, systems);
    }
}
