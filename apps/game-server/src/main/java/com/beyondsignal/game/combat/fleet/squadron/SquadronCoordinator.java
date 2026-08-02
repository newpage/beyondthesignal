package com.beyondsignal.game.combat.fleet.squadron;

import com.beyondsignal.game.combat.command.CombatCommand;
import com.beyondsignal.game.combat.engine.CombatEncounter;
import com.beyondsignal.game.combat.fleet.ai.FleetDecision;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class SquadronCoordinator {
    private final SquadronCommander commander;
    private final SquadronOrderExecutor executor;

    public SquadronCoordinator() {
        this(new SquadronCommander(), new SquadronOrderExecutor());
    }

    public SquadronCoordinator(
        SquadronCommander commander,
        SquadronOrderExecutor executor
    ) {
        this.commander = Objects.requireNonNull(
            commander,
            "commander"
        );
        this.executor = Objects.requireNonNull(
            executor,
            "executor"
        );
    }

    public SquadronCoordinationResult coordinate(
        CombatEncounter encounter,
        List<FleetDecision> decisions
    ) {
        Objects.requireNonNull(encounter, "encounter");
        Objects.requireNonNull(decisions, "decisions");

        List<SquadronExecutionPlan> plans = new ArrayList<>();
        List<CombatCommand> commands = new ArrayList<>();

        decisions.stream()
            .sorted(Comparator.comparing(
                decision -> decision.fleetId().toString()
            ))
            .forEach(decision -> encounter.fleet(decision.fleetId())
                .ifPresent(fleet -> {
                    var fleetPlans = commander.plan(
                        encounter,
                        fleet,
                        decision
                    );
                    plans.addAll(fleetPlans);
                    fleetPlans.forEach(plan ->
                        commands.addAll(executor.commands(
                            encounter,
                            plan
                        ))
                    );
                }));

        return new SquadronCoordinationResult(plans, commands);
    }
}
