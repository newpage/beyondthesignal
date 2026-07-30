package com.beyondsignal.game.simulation;

import java.time.Duration;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ShipSimulationEngine {
    private static final double MAX_SPEED_UNITS_PER_SECOND = 100.0;
    public static final int PHASER_COOLDOWN_TICKS = 10;

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
        EnumMap<ShipSubsystem, SubsystemState> subsystems =
            new EnumMap<>(current.subsystems());

        for (ShipCommand command : commands) {
            Objects.requireNonNull(command, "commands must not contain null");
            switch (command) {
                case SetHeadingCommand setHeading -> heading = setHeading.normalizedHeading();
                case SetThrottleCommand setThrottle -> throttle = setThrottle.throttle();
                case SetShieldsCommand setShields -> shieldsRaised = setShields.raised();
                case SelectTargetCommand selectTarget -> selectedTargetId = selectTarget.targetId();
                case ClearTargetCommand ignored -> selectedTargetId = null;
                case FireWeaponCommand ignored -> {
                    if (selectedTargetId != null && weaponCooldownTicks == 0) {
                        shotsFired++;
                        weaponCooldownTicks = PHASER_COOLDOWN_TICKS;
                    }
                }
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

        return new ShipState(
            current.sessionId(),
            current.tick() + 1,
            position,
            velocity,
            heading,
            throttle,
            shieldsRaised,
            selectedTargetId,
            weaponCooldownTicks,
            shotsFired,
            subsystems
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
