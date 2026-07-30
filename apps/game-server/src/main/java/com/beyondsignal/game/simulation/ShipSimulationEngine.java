package com.beyondsignal.game.simulation;

import java.time.Duration;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class ShipSimulationEngine {
    private static final double MAX_SPEED_UNITS_PER_SECOND = 100.0;
    public static final int PHASER_COOLDOWN_TICKS = 10;
    public static final int TORPEDO_COOLDOWN_TICKS = 24;
    public static final int PHASER_DAMAGE = 25;
    public static final int TORPEDO_DAMAGE = 60;
    public static final int SENSOR_SCAN_COOLDOWN_TICKS = 40;
    public static final int ENEMY_WEAPON_COOLDOWN_TICKS = 30;
    public static final int ENEMY_DAMAGE = 18;

    public ShipState tick(ShipState current, List<? extends ShipCommand> commands, Duration elapsed) {
        Objects.requireNonNull(current, "current must not be null");
        Objects.requireNonNull(commands, "commands must not be null");
        Objects.requireNonNull(elapsed, "elapsed must not be null");
        if (elapsed.isNegative() || elapsed.isZero()) throw new IllegalArgumentException("elapsed must be positive");

        double heading = current.headingDegrees();
        int throttle = current.destroyed() ? 0 : current.throttle();
        boolean shieldsRaised = current.shieldsRaised() && !current.destroyed();
        UUID selectedTargetId = current.selectedTargetId();
        int weaponCooldownTicks = Math.max(0, current.weaponCooldownTicks() - 1);
        long shotsFired = current.shotsFired();
        Map<UUID, CombatContactState> contacts = new LinkedHashMap<>(current.combatContacts());
        CombatEventState lastCombatEvent = current.lastCombatEvent();
        boolean redAlert = current.redAlert();
        int sensorCooldownTicks = Math.max(0, current.sensorCooldownTicks() - 1);
        long scansCompleted = current.scansCompleted();
        EnumMap<ShipSubsystem, SubsystemState> subsystems = new EnumMap<>(current.subsystems());
        Map<UUID, WorldObjectState> worldObjects = new LinkedHashMap<>(current.worldObjects());
        MissionState mission = current.mission();
        CrewAdvisory crewAdvisory = current.crewAdvisory();
        int shieldStrength = current.shieldStrength();
        int hullIntegrity = current.hullIntegrity();
        int enemyCooldown = Math.max(0, current.enemyWeaponCooldownTicks() - 1);
        int torpedoes = current.torpedoesRemaining();
        long enemyShots = current.enemyShotsFired();
        boolean destroyed = current.destroyed();

        if (!destroyed) {
            for (ShipCommand command : commands) {
                Objects.requireNonNull(command, "commands must not contain null");
                switch (command) {
                    case SetHeadingCommand c -> heading = c.normalizedHeading();
                    case SetThrottleCommand c -> throttle = c.throttle();
                    case SetShieldsCommand c -> shieldsRaised = c.raised();
                    case SelectTargetCommand c -> {
                        CombatContactState target = contacts.get(c.targetId());
                        if (target != null && !target.destroyed()) selectedTargetId = c.targetId();
                    }
                    case ClearTargetCommand ignored -> selectedTargetId = null;
                    case FireWeaponCommand c -> {
                        CombatContactState target = selectedTargetId == null ? null : contacts.get(selectedTargetId);
                        boolean torpedo = "TORPEDO".equals(c.weapon());
                        if (target != null && !target.destroyed() && weaponCooldownTicks == 0 && (!torpedo || torpedoes > 0)) {
                            int damage = torpedo ? TORPEDO_DAMAGE : PHASER_DAMAGE;
                            int shieldDamage = Math.min(target.shieldStrength(), damage);
                            int hullDamage = Math.min(target.hullIntegrity(), damage - shieldDamage);
                            int newShield = target.shieldStrength() - shieldDamage;
                            int newHull = target.hullIntegrity() - hullDamage;
                            boolean targetDestroyed = newHull == 0;
                            contacts.put(target.id(), new CombatContactState(target.id(), target.displayName(), newShield, newHull, targetDestroyed));
                            if (torpedo) torpedoes--;
                            shotsFired++;
                            weaponCooldownTicks = torpedo ? TORPEDO_COOLDOWN_TICKS : PHASER_COOLDOWN_TICKS;
                            lastCombatEvent = new CombatEventState(shotsFired, target.id(), c.weapon(), shieldDamage, hullDamage, targetDestroyed);
                            if (targetDestroyed) selectedTargetId = null;
                        }
                    }
                    case ScanContactsCommand ignored -> {
                        if (sensorCooldownTicks == 0) {
                            sensorCooldownTicks = SENSOR_SCAN_COOLDOWN_TICKS;
                            scansCompleted++;
                        }
                    }
                    case SetRedAlertCommand c -> redAlert = c.enabled();
                    case AllocatePowerCommand c -> {
                        SubsystemState existing = subsystems.get(c.subsystem());
                        subsystems.put(c.subsystem(), existing.withPowerAllocation(c.powerAllocation()));
                    }
                }
            }
        }

        validatePowerBudget(subsystems);
        SubsystemState engines = subsystems.get(ShipSubsystem.ENGINES);
        double effectiveEngineFactor = (engines.health() / 100.0) * (engines.powerAllocation() / 100.0);
        double speed = destroyed ? 0.0 : MAX_SPEED_UNITS_PER_SECOND * (throttle / 100.0) * effectiveEngineFactor;
        double radians = Math.toRadians(heading);
        Vector3 velocity = new Vector3(Math.cos(radians) * speed, Math.sin(radians) * speed, 0.0);
        double seconds = elapsed.toNanos() / 1_000_000_000.0;
        Vector3 position = current.position().add(velocity.scale(seconds));
        worldObjects.replaceAll((id, object) -> object.advance(seconds));

        CombatContactState hostile = contacts.values().stream().filter(c -> !c.destroyed()).findFirst().orElse(null);
        if (!destroyed && hostile != null && redAlert && enemyCooldown == 0 && current.tick() > 0) {
            int incoming = ENEMY_DAMAGE;
            int absorbed = shieldsRaised ? Math.min(shieldStrength, incoming) : 0;
            shieldStrength -= absorbed;
            hullIntegrity = Math.max(0, hullIntegrity - (incoming - absorbed));
            destroyed = hullIntegrity == 0;
            enemyCooldown = ENEMY_WEAPON_COOLDOWN_TICKS;
            enemyShots++;
            crewAdvisory = new CrewAdvisory(crewAdvisory.sequence() + 1, "TACTICAL", destroyed ? "CRITICAL" : "WARNING",
                destroyed ? "Ship destroyed. Mission failed." : "Enemy weapons impact. Shields and hull report updated.");
        }

        long nextTick = current.tick() + 1;
        boolean targetDestroyed = contacts.values().stream().filter(c -> c.displayName().equals("Training Drone")).anyMatch(CombatContactState::destroyed);
        if (destroyed && mission.status() == MissionStatus.ACTIVE) {
            mission = mission.fail(nextTick);
        } else if (targetDestroyed && mission.status() == MissionStatus.ACTIVE) {
            mission = mission.complete(nextTick, 1000);
            crewAdvisory = new CrewAdvisory(crewAdvisory.sequence() + 1, "CAPTAIN", "SUCCESS",
                "Mission complete. Hostile neutralized. Return to Outpost Meridian.");
        } else if (redAlert && !current.redAlert()) {
            crewAdvisory = new CrewAdvisory(crewAdvisory.sequence() + 1, "TACTICAL", "WARNING",
                "Red alert active. Raise shields and prepare weapons.");
        } else if (scansCompleted > current.scansCompleted()) {
            crewAdvisory = new CrewAdvisory(crewAdvisory.sequence() + 1, "SCIENCE", "INFO",
                "Scan complete. Hostile NPC ship identified near bearing 006.");
        } else if (subsystems.get(ShipSubsystem.ENGINES).powerAllocation() < 15) {
            crewAdvisory = new CrewAdvisory(crewAdvisory.sequence() + 1, "ENGINEERING", "WARNING",
                "Engine power is below maneuvering recommendation.");
        }

        return new ShipState(current.sessionId(), nextTick, position, velocity, heading, throttle, shieldsRaised,
            selectedTargetId, weaponCooldownTicks, shotsFired, contacts, lastCombatEvent, redAlert,
            sensorCooldownTicks, scansCompleted, subsystems, worldObjects, mission, crewAdvisory,
            shieldStrength, hullIntegrity, enemyCooldown, torpedoes, enemyShots, destroyed);
    }

    private static void validatePowerBudget(Map<ShipSubsystem, SubsystemState> subsystems) {
        int total = subsystems.values().stream().mapToInt(SubsystemState::powerAllocation).sum();
        if (total > 100) throw new IllegalArgumentException("total subsystem power allocation must not exceed 100; was " + total);
    }
}
