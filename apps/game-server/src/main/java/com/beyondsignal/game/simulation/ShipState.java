package com.beyondsignal.game.simulation;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
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
    int weaponCooldownTicks,
    long shotsFired,
    Map<UUID, CombatContactState> combatContacts,
    CombatEventState lastCombatEvent,
    boolean redAlert,
    int sensorCooldownTicks,
    long scansCompleted,
    Map<ShipSubsystem, SubsystemState> subsystems,
    Map<UUID, WorldObjectState> worldObjects,
    MissionState mission,
    CrewAdvisory crewAdvisory,
    int shieldStrength,
    int hullIntegrity,
    int enemyWeaponCooldownTicks,
    int torpedoesRemaining,
    long enemyShotsFired,
    boolean destroyed
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
        if (weaponCooldownTicks < 0) {
            throw new IllegalArgumentException("weaponCooldownTicks must not be negative");
        }
        if (shotsFired < 0) {
            throw new IllegalArgumentException("shotsFired must not be negative");
        }
        if (sensorCooldownTicks < 0) {
            throw new IllegalArgumentException("sensorCooldownTicks must not be negative");
        }
        if (scansCompleted < 0) {
            throw new IllegalArgumentException("scansCompleted must not be negative");
        }
        if (shieldStrength < 0 || shieldStrength > 100) {
            throw new IllegalArgumentException("shieldStrength must be between 0 and 100");
        }
        if (hullIntegrity < 0 || hullIntegrity > 100) {
            throw new IllegalArgumentException("hullIntegrity must be between 0 and 100");
        }
        if (enemyWeaponCooldownTicks < 0) {
            throw new IllegalArgumentException("enemyWeaponCooldownTicks must not be negative");
        }
        if (torpedoesRemaining < 0) {
            throw new IllegalArgumentException("torpedoesRemaining must not be negative");
        }
        if (enemyShotsFired < 0) {
            throw new IllegalArgumentException("enemyShotsFired must not be negative");
        }
        if (destroyed != (hullIntegrity == 0)) {
            throw new IllegalArgumentException("destroyed must match zero hull integrity");
        }

        Map<UUID, CombatContactState> contactCopy = Map.copyOf(
            Objects.requireNonNull(combatContacts, "combatContacts must not be null")
        );
        for (Map.Entry<UUID, CombatContactState> entry : contactCopy.entrySet()) {
            if (!entry.getKey().equals(entry.getValue().id())) {
                throw new IllegalArgumentException("combat contact key must match contact id");
            }
        }
        combatContacts = contactCopy;

        EnumMap<ShipSubsystem, SubsystemState> copy = new EnumMap<>(ShipSubsystem.class);
        copy.putAll(Objects.requireNonNull(subsystems, "subsystems must not be null"));
        for (ShipSubsystem subsystem : ShipSubsystem.values()) {
            if (!copy.containsKey(subsystem)) {
                throw new IllegalArgumentException("missing subsystem state: " + subsystem);
            }
        }
        subsystems = Collections.unmodifiableMap(copy);

        Map<UUID, WorldObjectState> worldCopy = Map.copyOf(
            Objects.requireNonNull(worldObjects, "worldObjects must not be null")
        );
        for (Map.Entry<UUID, WorldObjectState> entry : worldCopy.entrySet()) {
            if (!entry.getKey().equals(entry.getValue().id())) {
                throw new IllegalArgumentException("world object key must match object id");
            }
        }
        worldObjects = worldCopy;
        Objects.requireNonNull(mission, "mission must not be null");
        Objects.requireNonNull(crewAdvisory, "crewAdvisory must not be null");
    }

    public static ShipState initial(UUID sessionId) {
        EnumMap<ShipSubsystem, SubsystemState> systems = new EnumMap<>(ShipSubsystem.class);
        systems.put(ShipSubsystem.ENGINES, SubsystemState.nominal(40));
        systems.put(ShipSubsystem.SHIELDS, SubsystemState.nominal(20));
        systems.put(ShipSubsystem.SENSORS, SubsystemState.nominal(15));
        systems.put(ShipSubsystem.WEAPONS, SubsystemState.nominal(15));
        systems.put(ShipSubsystem.LIFE_SUPPORT, SubsystemState.nominal(10));

        UUID trainingTargetId = UUID.nameUUIDFromBytes(
            ("training-contact:" + sessionId).getBytes(StandardCharsets.UTF_8)
        );
        Map<UUID, CombatContactState> contacts = Map.of(
            trainingTargetId,
            CombatContactState.trainingDrone(trainingTargetId)
        );
        UUID starId = UUID.nameUUIDFromBytes(("star:" + sessionId).getBytes(StandardCharsets.UTF_8));
        UUID stationId = UUID.nameUUIDFromBytes(("station:" + sessionId).getBytes(StandardCharsets.UTF_8));
        UUID droneWorldId = trainingTargetId;
        Map<UUID, WorldObjectState> world = Map.of(
            starId, new WorldObjectState(starId, "Helios", "STAR", new Vector3(2500, 0, 0), Vector3.ZERO, false),
            stationId, new WorldObjectState(stationId, "Outpost Meridian", "STATION", new Vector3(500, 800, 0), Vector3.ZERO, false),
            droneWorldId, new WorldObjectState(droneWorldId, "Training Drone", "NPC_SHIP", new Vector3(1000, 100, 0), new Vector3(-2, 1, 0), true)
        );
        UUID missionId = UUID.nameUUIDFromBytes(("mission:" + sessionId).getBytes(StandardCharsets.UTF_8));
        MissionState mission = new MissionState(
            missionId,
            "First Contact Drill",
            "Locate and destroy the hostile Training Drone",
            MissionStatus.ACTIVE,
            0,
            0,
            null
        );
        CrewAdvisory advisory = new CrewAdvisory(0, "CAPTAIN", "INFO", "Mission initialized. Awaiting crew action.");

        return new ShipState(
            sessionId, 0, Vector3.ZERO, Vector3.ZERO, 0.0, 0, false, null,
            0, 0, contacts, null, false, 0, 0, systems, world, mission, advisory,
            100, 100, 30, 6, 0, false
        );
    }
}
