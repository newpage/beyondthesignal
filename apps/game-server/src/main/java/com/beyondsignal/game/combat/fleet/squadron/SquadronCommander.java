package com.beyondsignal.game.combat.fleet.squadron;

import com.beyondsignal.game.combat.engine.CombatEncounter;
import com.beyondsignal.game.combat.engine.CombatParticipant;
import com.beyondsignal.game.combat.fleet.FleetState;
import com.beyondsignal.game.combat.fleet.SquadronState;
import com.beyondsignal.game.combat.fleet.ai.FleetDecision;
import com.beyondsignal.game.combat.fleet.ai.FleetObjectiveType;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public final class SquadronCommander {
    private final SquadronTargetSelector targetSelector;
    private final SquadronMoraleCalculator moraleCalculator;

    public SquadronCommander() {
        this(
            new SquadronTargetSelector(),
            new SquadronMoraleCalculator()
        );
    }

    public SquadronCommander(
        SquadronTargetSelector targetSelector,
        SquadronMoraleCalculator moraleCalculator
    ) {
        this.targetSelector = Objects.requireNonNull(
            targetSelector,
            "targetSelector"
        );
        this.moraleCalculator = Objects.requireNonNull(
            moraleCalculator,
            "moraleCalculator"
        );
    }

    public java.util.List<SquadronExecutionPlan> plan(
        CombatEncounter encounter,
        FleetState fleet,
        FleetDecision decision
    ) {
        Objects.requireNonNull(encounter, "encounter");
        Objects.requireNonNull(fleet, "fleet");
        Objects.requireNonNull(decision, "decision");

        return fleet.squadrons().stream()
            .sorted(Comparator.comparing(
                squadron -> squadron.squadronId().toString()
            ))
            .map(squadron -> planSquadron(
                encounter,
                fleet,
                squadron,
                decision
            ))
            .toList();
    }

    private SquadronExecutionPlan planSquadron(
        CombatEncounter encounter,
        FleetState fleet,
        SquadronState squadron,
        FleetDecision decision
    ) {
        double morale = moraleCalculator.calculate(encounter, squadron);
        SquadronExecutionMode mode = mode(decision, morale);
        var members = squadron.memberIds().stream()
            .map(encounter::participant)
            .flatMap(Optional::stream)
            .filter(CombatParticipant::operational)
            .map(CombatParticipant::participantId)
            .sorted()
            .toList();
        var target = mode == SquadronExecutionMode.FOCUS_FIRE
            || mode == SquadronExecutionMode.DEFEND
                ? targetSelector.select(encounter, decision)
                : null;

        return new SquadronExecutionPlan(
            fleet.fleetId(),
            squadron.squadronId(),
            mode,
            target,
            members,
            morale,
            decision.generatedTick()
        );
    }

    private static SquadronExecutionMode mode(
        FleetDecision decision,
        double morale
    ) {
        if (decision.retreat() || morale < 0.30) {
            return SquadronExecutionMode.WITHDRAW;
        }
        return switch (decision.objective()) {
            case ENGAGE_PRIMARY_THREAT ->
                SquadronExecutionMode.FOCUS_FIRE;
            case DEFEND_FLAGSHIP ->
                SquadronExecutionMode.DEFEND;
            case HOLD_POSITION ->
                SquadronExecutionMode.HOLD;
            case RETREAT ->
                SquadronExecutionMode.WITHDRAW;
        };
    }
}
