package com.beyondsignal.game.simulation;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShipSimulationEngineTest {
    private final ShipSimulationEngine engine = new ShipSimulationEngine();

    @Test
    void advancesShipUsingHeadingThrottleAndEnginePower() {
        ShipState initial = ShipState.initial(UUID.randomUUID());

        ShipState next = engine.tick(
            initial,
            List.of(
                new SetHeadingCommand(90.0),
                new SetThrottleCommand(50)
            ),
            Duration.ofSeconds(2)
        );

        assertThat(next.tick()).isEqualTo(1);
        assertThat(next.headingDegrees()).isEqualTo(90.0);
        assertThat(next.throttle()).isEqualTo(50);
        assertThat(next.velocity().x()).isCloseTo(0.0, within(0.000001));
        assertThat(next.velocity().y()).isCloseTo(20.0, within(0.000001));
        assertThat(next.position().y()).isCloseTo(40.0, within(0.000001));
    }

    @Test
    void producesIdenticalStateForIdenticalInput() {
        UUID sessionId = UUID.randomUUID();
        ShipState initial = ShipState.initial(sessionId);
        List<ShipCommand> commands = List.of(
            new SetHeadingCommand(225.0),
            new SetThrottleCommand(80)
        );

        ShipState first = engine.tick(initial, commands, Duration.ofMillis(50));
        ShipState second = engine.tick(initial, commands, Duration.ofMillis(50));

        assertThat(first).isEqualTo(second);
    }

    @Test
    void normalizesNegativeHeading() {
        ShipState next = engine.tick(
            ShipState.initial(UUID.randomUUID()),
            List.of(new SetHeadingCommand(-90.0)),
            Duration.ofMillis(50)
        );

        assertThat(next.headingDegrees()).isEqualTo(270.0);
    }

    @Test
    void appliesTacticalShieldAndTargetCommandsAuthoritatively() {
        ShipState initial = ShipState.initial(UUID.randomUUID());
        UUID targetId = initial.combatContacts().keySet().iterator().next();

        ShipState selected = engine.tick(
            initial,
            List.of(new SetShieldsCommand(true), new SelectTargetCommand(targetId)),
            Duration.ofMillis(50)
        );

        assertThat(selected.shieldsRaised()).isTrue();
        assertThat(selected.selectedTargetId()).isEqualTo(targetId);

        ShipState cleared = engine.tick(
            selected,
            List.of(new SetShieldsCommand(false), new ClearTargetCommand()),
            Duration.ofMillis(50)
        );

        assertThat(cleared.shieldsRaised()).isFalse();
        assertThat(cleared.selectedTargetId()).isNull();
    }


    @Test
    void firesPhaserAndAppliesAuthoritativeCooldown() {
        ShipState initial = ShipState.initial(UUID.randomUUID());
        UUID targetId = initial.combatContacts().keySet().iterator().next();
        ShipState targeted = engine.tick(
            initial,
            List.of(new SelectTargetCommand(targetId)),
            Duration.ofMillis(50)
        );

        ShipState fired = engine.tick(
            targeted,
            List.of(new FireWeaponCommand("PHASER")),
            Duration.ofMillis(50)
        );

        assertThat(fired.shotsFired()).isEqualTo(1);
        assertThat(fired.weaponCooldownTicks()).isEqualTo(ShipSimulationEngine.PHASER_COOLDOWN_TICKS);
        CombatContactState damaged = fired.combatContacts().get(targetId);
        assertThat(damaged.shieldStrength()).isEqualTo(15);
        assertThat(damaged.hullIntegrity()).isEqualTo(100);
        assertThat(fired.lastCombatEvent().shieldDamage()).isEqualTo(25);
        assertThat(fired.lastCombatEvent().hullDamage()).isZero();

        ShipState duplicateDuringCooldown = engine.tick(
            fired,
            List.of(new FireWeaponCommand("PHASER")),
            Duration.ofMillis(50)
        );
        assertThat(duplicateDuringCooldown.shotsFired()).isEqualTo(1);
        assertThat(duplicateDuringCooldown.weaponCooldownTicks())
            .isEqualTo(ShipSimulationEngine.PHASER_COOLDOWN_TICKS - 1);
    }

    @Test
    void destroysTargetAfterShieldAndHullDamageAndClearsSelection() {
        ShipState state = ShipState.initial(UUID.randomUUID());
        UUID targetId = state.combatContacts().keySet().iterator().next();
        state = engine.tick(state, List.of(new SelectTargetCommand(targetId)), Duration.ofMillis(50));

        for (int shot = 0; shot < 6; shot++) {
            while (state.weaponCooldownTicks() > 0) {
                state = engine.tick(state, List.of(), Duration.ofMillis(50));
            }
            state = engine.tick(state, List.of(new FireWeaponCommand("PHASER")), Duration.ofMillis(50));
        }

        CombatContactState destroyed = state.combatContacts().get(targetId);
        assertThat(destroyed.shieldStrength()).isZero();
        assertThat(destroyed.hullIntegrity()).isZero();
        assertThat(destroyed.destroyed()).isTrue();
        assertThat(state.selectedTargetId()).isNull();
        assertThat(state.lastCombatEvent().targetDestroyed()).isTrue();
    }

    @Test
    void rejectsPowerAllocationsAboveShipBudget() {
        ShipState initial = ShipState.initial(UUID.randomUUID());

        assertThatThrownBy(() -> engine.tick(
            initial,
            List.of(new AllocatePowerCommand(ShipSubsystem.ENGINES, 90)),
            Duration.ofMillis(50)
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("must not exceed 100");
    }

    @Test
    void returnedSubsystemMapCannotBeModified() {
        ShipState state = ShipState.initial(UUID.randomUUID());

        assertThatThrownBy(() -> state.subsystems().put(
            ShipSubsystem.ENGINES,
            SubsystemState.nominal(0)
        )).isInstanceOf(UnsupportedOperationException.class);
    }

    private static org.assertj.core.data.Offset<Double> within(double value) {
        return org.assertj.core.data.Offset.offset(value);
    }
}
