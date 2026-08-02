package com.beyondsignal.game.combat.engine;

import com.beyondsignal.game.combat.command.CombatCommand;
import com.beyondsignal.game.combat.command.FireCombatWeaponCommand;
import com.beyondsignal.game.combat.command.NoOpCombatCommand;
import com.beyondsignal.game.combat.command.SelectCombatTargetCommand;
import com.beyondsignal.game.combat.event.CombatEvent;
import com.beyondsignal.game.combat.weapon.WeaponMountState;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class CombatCommandProcessor {
    private final TargetManager targetManager;
    private final FireControlComputer fireControl;
    private final CombatEventFactory eventFactory;
    private final BeamDamageResolver beamDamageResolver;

    public CombatCommandProcessor() {
        this(
            new TargetManager(),
            new FireControlComputer(),
            new CombatEventFactory(),
            new BeamDamageResolver()
        );
    }

    public CombatCommandProcessor(
        TargetManager targetManager,
        FireControlComputer fireControl,
        CombatEventFactory eventFactory
    ) {
        this(
            targetManager,
            fireControl,
            eventFactory,
            new BeamDamageResolver()
        );
    }

    public CombatCommandProcessor(
        TargetManager targetManager,
        FireControlComputer fireControl,
        CombatEventFactory eventFactory,
        BeamDamageResolver beamDamageResolver
    ) {
        this.targetManager = Objects.requireNonNull(
            targetManager,
            "targetManager"
        );
        this.fireControl = Objects.requireNonNull(
            fireControl,
            "fireControl"
        );
        this.eventFactory = Objects.requireNonNull(
            eventFactory,
            "eventFactory"
        );
        this.beamDamageResolver = Objects.requireNonNull(
            beamDamageResolver,
            "beamDamageResolver"
        );
    }

    public List<CombatEvent> process(
        CombatEncounter encounter,
        CombatCommand command
    ) {
        CombatParticipant actor = encounter.participant(command.actorId())
            .orElseThrow(() -> new IllegalArgumentException("Unknown actor"));

        if (command instanceof NoOpCombatCommand) {
            return List.of();
        }
        if (command instanceof SelectCombatTargetCommand select) {
            targetManager.selectTarget(encounter, actor, select.targetId());
            CombatParticipant target = encounter.participant(select.targetId()).orElseThrow();
            return List.of(eventFactory.targetSelected(encounter, actor, target));
        }
        if (command instanceof FireCombatWeaponCommand fire) {
            return fire(encounter, actor, fire);
        }

        throw new IllegalArgumentException(
            "Unsupported combat command: " + command.getClass().getSimpleName()
        );
    }

    private List<CombatEvent> fire(
        CombatEncounter encounter,
        CombatParticipant actor,
        FireCombatWeaponCommand command
    ) {
        UUID targetId = actor.selectedTargetId()
            .orElseThrow(() -> new IllegalStateException("No target selected"));
        CombatParticipant target = encounter.participant(targetId)
            .orElseThrow(() -> new IllegalStateException("Selected target no longer exists"));

        WeaponMountState weapon = actor.weapon(command.weaponMountId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Unknown weapon mount: " + command.weaponMountId()
            ));

        FireSolution solution = fireControl.calculate(
            actor,
            target,
            weapon,
            command.distance(),
            command.powerModifier(),
            encounter.context().random()
        );

        actor.replaceWeapon(weapon.fire());

        List<CombatEvent> events = new ArrayList<>();
        events.add(eventFactory.weaponFired(encounter, solution));

        if (weapon.definition().type()
            == com.beyondsignal.game.combat.weapon.WeaponType.BEAM) {
            events.add(eventFactory.beamFired(encounter, solution));
        }

        events.add(eventFactory.weaponResult(encounter, solution));

        if (!solution.hit()) {
            return List.copyOf(events);
        }

        if (weapon.definition().type()
            != com.beyondsignal.game.combat.weapon.WeaponType.BEAM) {
            return List.copyOf(events);
        }

        BeamDamageResolution resolution = beamDamageResolver.resolve(
            target,
            weapon.definition()
        );
        events.add(eventFactory.beamHit(encounter, solution, resolution));
        events.add(eventFactory.shieldImpact(encounter, solution, resolution));

        if (resolution.shieldCollapsedNow()) {
            events.add(eventFactory.shieldCollapsed(
                encounter,
                solution,
                resolution
            ));
        }
        if (resolution.hullDamage() > 0) {
            events.add(eventFactory.hullDamage(
                encounter,
                solution,
                resolution
            ));
        }
        if (resolution.targetDestroyed()) {
            events.add(eventFactory.shipDestroyed(encounter, solution));
        }

        return List.copyOf(events);
    }
}
