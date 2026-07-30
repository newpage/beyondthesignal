package com.beyondsignal.game.simulation;

import java.time.Duration;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ShipSimulationEngine {
    private static final double MAX_SPEED_UNITS_PER_SECOND = 100.0;
    public static final int PHASER_COOLDOWN_TICKS = 10;
    public static final int PHASER_DAMAGE = 25;
    public static final int SENSOR_SCAN_COOLDOWN_TICKS = 40;

    public ShipState tick(ShipState current, List<? extends ShipCommand> commands, Duration elapsed) {
        Objects.requireNonNull(current, "current must not be null");
        Objects.requireNonNull(commands, "commands must not be null");
        Objects.requireNonNull(elapsed, "elapsed must not be null");

        if (elapsed.isNegative() || elapsed.isZero()) {
            throw new IllegalArgumentException("elapsed must be positive");
        }

        double heading = current.headingDegrees();
        int throttle = current.throttle();
        boolean shieldsRaised = current.shieldsRaised();
        java.util.UUID selectedTargetId = current.selectedTargetId();
        int weaponCooldownTicks = Math.max(0, current.weaponCooldownTicks() - 1);
        long shotsFired = current.shotsFired();
        java.util.Map<java.util.UUID, CombatContactState> combatContacts =
            new java.util.LinkedHashMap<>(current.combatContacts());
        CombatEventState lastCombatEvent = current.lastCombatEvent();
        boolean redAlert = current.redAlert();
        int sensorCooldownTicks = Math.max(0, current.sensorCooldownTicks() - 1);
        long scansCompleted = current.scansCompleted();
        EnumMap<ShipSubsystem, SubsystemState> subsystems =
            new EnumMap<>(current.subsystems());
        java.util.Map<java.util.UUID, WorldObjectState> worldObjects =
            new java.util.LinkedHashMap<>(current.worldObjects());
        MissionState mission = current.mission();
        CrewAdvisory crewAdvisory = current.crewAdvisory();

        for (ShipCommand command : commands) {
            Objects.requireNonNull(command, "commands must not contain null");
            switch (command) {
                case SetHeadingCommand setHeading -> heading = setHeading.normalizedHeading();
                case SetThrottleCommand setThrottle -> throttle = setThrottle.throttle();
                case SetShieldsCommand setShields -> shieldsRaised = setShields.raised();
                case SelectTargetCommand selectTarget -> {
                    CombatContactState contact = combatContacts.get(selectTarget.targetId());
                    if (contact != null && !contact.destroyed()) {
                        selectedTargetId = selectTarget.targetId();
                    }
                }
                case ClearTargetCommand ignored -> selectedTargetId = null;
                case FireWeaponCommand fireWeapon -> {
                    CombatContactState target = selectedTargetId == null
                        ? null
                        : combatContacts.get(selectedTargetId);
                    if (target != null && !target.destroyed() && weaponCooldownTicks == 0) {
                        int shieldDamage = Math.min(target.shieldStrength(), PHASER_DAMAGE);
                        int remainingDamage = PHASER_DAMAGE - shieldDamage;
                        int hullDamage = Math.min(target.hullIntegrity(), remainingDamage);
                        int newShield = target.shieldStrength() - shieldDamage;
                        int newHull = target.hullIntegrity() - hullDamage;
                        boolean destroyed = newHull == 0;

                        combatContacts.put(target.id(), new CombatContactState(
                            target.id(), target.displayName(), newShield, newHull, destroyed
                        ));
                        shotsFired++;
                        weaponCooldownTicks = PHASER_COOLDOWN_TICKS;
                        lastCombatEvent = new CombatEventState(
                            shotsFired,
                            target.id(),
                            fireWeapon.weapon(),
                            shieldDamage,
                            hullDamage,
                            destroyed
                        );
                        if (destroyed) {
                            selectedTargetId = null;
                        }
                    }
                }
                case ScanContactsCommand ignored -> {
                    if (sensorCooldownTicks == 0) {
                        sensorCooldownTicks = SENSOR_SCAN_COOLDOWN_TICKS;
                        scansCompleted++;
                    }
                }
                case SetRedAlertCommand setRedAlert -> redAlert = setRedAlert.enabled();
                case AllocatePowerCommand allocatePower -> {
                    SubsystemState existing = subsystems.get(allocatePower.subsystem());
                    subsystems.put(
                        allocatePower.subsystem(),
                        existing.withPowerAllocation(allocatePower.powerAllocation())
                    );
                }
            }
        }

        validatePowerBudget(subsystems);

        SubsystemState engines = subsystems.get(ShipSubsystem.ENGINES);
        double effectiveEngineFactor =
            (engines.health() / 100.0) * (engines.powerAllocation() / 100.0);
        double speed = MAX_SPEED_UNITS_PER_SECOND
            * (throttle / 100.0)
            * effectiveEngineFactor;

        double radians = Math.toRadians(heading);
        Vector3 velocity = new Vector3(
            Math.cos(radians) * speed,
            Math.sin(radians) * speed,
            0.0
        );

        double seconds = elapsed.toNanos() / 1_000_000_000.0;
        Vector3 position = current.position().add(velocity.scale(seconds));

        worldObjects.replaceAll((id, object) -> object.advance(seconds));
        long nextTick = current.tick() + 1;
        boolean targetDestroyed = combatContacts.values().stream()
            .filter(contact -> contact.displayName().equals("Training Drone"))
            .anyMatch(CombatContactState::destroyed);
        if (targetDestroyed && mission.status() == MissionStatus.ACTIVE) {
            mission = mission.complete(nextTick, 1000);
            crewAdvisory = new CrewAdvisory(
                crewAdvisory.sequence() + 1,
                "CAPTAIN",
                "SUCCESS",
                "Mission complete. Training Drone destroyed. Return to Outpost Meridian."
            );
        } else if (redAlert && !current.redAlert()) {
            crewAdvisory = new CrewAdvisory(
                crewAdvisory.sequence() + 1,
                "TACTICAL",
                "WARNING",
                "Red alert active. Recommend shields raised and weapons power at 20% or higher."
            );
        } else if (scansCompleted > current.scansCompleted()) {
            crewAdvisory = new CrewAdvisory(
                crewAdvisory.sequence() + 1,
                "SCIENCE",
                "INFO",
                "Scan complete. Hostile NPC ship identified near bearing 006."
            );
        } else if (subsystems.get(ShipSubsystem.ENGINES).powerAllocation() < 15) {
            crewAdvisory = new CrewAdvisory(
                crewAdvisory.sequence() + 1,
                "ENGINEERING",
                "WARNING",
                "Engine power is below maneuvering recommendation."
            );
        }

        return new ShipState(
            current.sessionId(),
            nextTick,
            position,
            velocity,
            heading,
            throttle,
            shieldsRaised,
            selectedTargetId,
            weaponCooldownTicks,
            shotsFired,
            combatContacts,
            lastCombatEvent,
            redAlert,
            sensorCooldownTicks,
            scansCompleted,
            subsystems,
            worldObjects,
            mission,
            crewAdvisory
        );
    }

    private static void validatePowerBudget(Map<ShipSubsystem, SubsystemState> subsystems) {
        int total = subsystems.values().stream()
            .mapToInt(SubsystemState::powerAllocation)
            .sum();

        if (total > 100) {
            throw new IllegalArgumentException(
                "total subsystem power allocation must not exceed 100; was " + total
            );
        }
    }
}
