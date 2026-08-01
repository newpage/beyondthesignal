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

    public CombatCommandProcessor() {
        this(new TargetManager(), new FireControlComputer(), new CombatEventFactory());
    }

    public CombatCommandProcessor(
        TargetManager targetManager,
        FireControlComputer fireControl,
        CombatEventFactory eventFactory
    ) {
        this.targetManager = Objects.requireNonNull(targetManager, "targetManager");
        this.fireControl = Objects.requireNonNull(fireControl, "fireControl");
        this.eventFactory = Objects.requireNonNull(eventFactory, "eventFactory");
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
        CombatEvent fired = eventFactory.weaponFired(encounter, solution);
        events.add(fired);
        encounter.appendEvent(fired);

        CombatEvent result = eventFactory.weaponResult(encounter, solution);
        events.add(result);
        encounter.appendEvent(result);

        return events;
    }
}
