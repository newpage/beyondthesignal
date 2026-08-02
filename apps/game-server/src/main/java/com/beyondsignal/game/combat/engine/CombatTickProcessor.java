package com.beyondsignal.game.combat.engine;

import com.beyondsignal.game.combat.command.CombatCommand;
import com.beyondsignal.game.combat.event.CombatEvent;
import com.beyondsignal.game.combat.fleet.ai.FleetCommanderAI;
import com.beyondsignal.game.combat.fleet.squadron.SquadronCoordinator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.beyondsignal.game.combat.projectile.ProjectileLifecycleProcessor;

/**
 * Deterministic combat tick processor.
 */
public final class CombatTickProcessor {
    private final CombatCommandProcessor commandProcessor;
    private final ProjectileLifecycleProcessor projectileProcessor;
    private final FleetCommanderAI fleetCommander;
    private final SquadronCoordinator squadronCoordinator;

    public CombatTickProcessor() {
        this(
            new CombatCommandProcessor(),
            new ProjectileLifecycleProcessor(),
            new FleetCommanderAI(),
            new SquadronCoordinator()
        );
    }

    public CombatTickProcessor(CombatCommandProcessor commandProcessor) {
        this(
            commandProcessor,
            new ProjectileLifecycleProcessor(),
            new FleetCommanderAI(),
            new SquadronCoordinator()
        );
    }

    public CombatTickProcessor(
        CombatCommandProcessor commandProcessor,
        ProjectileLifecycleProcessor projectileProcessor
    ) {
        this(
            commandProcessor,
            projectileProcessor,
            new FleetCommanderAI(),
            new SquadronCoordinator()
        );
    }

    public CombatTickProcessor(
        CombatCommandProcessor commandProcessor,
        ProjectileLifecycleProcessor projectileProcessor,
        FleetCommanderAI fleetCommander
    ) {
        this(
            commandProcessor,
            projectileProcessor,
            fleetCommander,
            new SquadronCoordinator()
        );
    }

    public CombatTickProcessor(
        CombatCommandProcessor commandProcessor,
        ProjectileLifecycleProcessor projectileProcessor,
        FleetCommanderAI fleetCommander,
        SquadronCoordinator squadronCoordinator
    ) {
        this.commandProcessor = Objects.requireNonNull(
            commandProcessor,
            "commandProcessor"
        );
        this.projectileProcessor = Objects.requireNonNull(
            projectileProcessor,
            "projectileProcessor"
        );
        this.fleetCommander = Objects.requireNonNull(
            fleetCommander,
            "fleetCommander"
        );
        this.squadronCoordinator = Objects.requireNonNull(
            squadronCoordinator,
            "squadronCoordinator"
        );
    }

    public CombatTickResult process(CombatEncounter encounter) {
        Objects.requireNonNull(encounter, "encounter");
        if (encounter.status() != CombatEncounterStatus.ACTIVE) {
            throw new IllegalStateException("Encounter is not active");
        }

        long tick = encounter.context().clock().advance();

        for (CombatParticipant participant : encounter.participants()) {
            participant.tickWeapons();
            participant.shields().regenerate();
        }

        List<CombatCommand> commands = encounter.drainCommands();
        List<CombatEvent> produced = new ArrayList<>();

        for (CombatCommand command : commands) {
            appendSequenced(
                encounter,
                produced,
                commandProcessor.process(encounter, command)
            );
        }

        appendSequenced(
            encounter,
            produced,
            projectileProcessor.advance(encounter, tick)
        );

        var fleetDecisions = fleetCommander.evaluate(encounter, tick);
        for (var decision : fleetDecisions) {
            encounter.recordFleetDecision(decision);
        }

        var coordination = squadronCoordinator.coordinate(
            encounter,
            fleetDecisions
        );
        for (CombatCommand command : coordination.commands()) {
            encounter.submit(command);
        }

        encounter.evaluateCompletion();

        return new CombatTickResult(
            tick,
            commands.size(),
            produced,
            encounter.status()
        );
    }

    private static void appendSequenced(
        CombatEncounter encounter,
        List<CombatEvent> produced,
        List<CombatEvent> events
    ) {
        for (CombatEvent event : events) {
            CombatEvent sequenced = withSequence(
                event,
                encounter.nextEventSequence()
            );
            encounter.appendEvent(sequenced);
            produced.add(sequenced);
        }
    }

    private static CombatEvent withSequence(
        CombatEvent event,
        long sequence
    ) {
        return new CombatEvent(
            event.combatId(),
            sequence,
            event.tick(),
            event.sourceId(),
            event.targetId(),
            event.type(),
            event.occurredAt(),
            event.payload()
        );
    }
}
