package com.beyondsignal.game.combat.ai.fleet.formation;

import com.beyondsignal.game.combat.ai.fleet.FleetDefinition;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CombatAwareFormationCoordinator {
    private final FleetFormationCoordinator fleetFormationCoordinator;
    private final FormationCombatDecisionEngine decisionEngine;
    private final FormationCasualtyProcessor casualtyProcessor;
    private final CommanderProtectionPlanner protectionPlanner;

    public CombatAwareFormationCoordinator() {
        this(
            new FleetFormationCoordinator(),
            new FormationCombatDecisionEngine(),
            new FormationCasualtyProcessor(),
            new CommanderProtectionPlanner()
        );
    }

    public CombatAwareFormationCoordinator(
        FleetFormationCoordinator fleetFormationCoordinator,
        FormationCombatDecisionEngine decisionEngine,
        FormationCasualtyProcessor casualtyProcessor,
        CommanderProtectionPlanner protectionPlanner
    ) {
        this.fleetFormationCoordinator = Objects.requireNonNull(
            fleetFormationCoordinator,
            "fleetFormationCoordinator"
        );
        this.decisionEngine = Objects.requireNonNull(decisionEngine, "decisionEngine");
        this.casualtyProcessor = Objects.requireNonNull(
            casualtyProcessor,
            "casualtyProcessor"
        );
        this.protectionPlanner = Objects.requireNonNull(
            protectionPlanner,
            "protectionPlanner"
        );
    }

    public FormationLifecycleUpdate update(
        FleetDefinition fleet,
        FormationPlan currentPlan,
        FormationCasualtyUpdate casualties,
        List<FormationMemberState> memberStates,
        List<FormationMemberCombatState> combatStates,
        FormationCombatPolicy policy,
        boolean commanderUnderThreat
    ) {
        Objects.requireNonNull(fleet, "fleet");
        Objects.requireNonNull(currentPlan, "currentPlan");
        Objects.requireNonNull(casualties, "casualties");
        Objects.requireNonNull(memberStates, "memberStates");
        Objects.requireNonNull(combatStates, "combatStates");
        Objects.requireNonNull(policy, "policy");

        FormationPlan activePlan = casualtyProcessor.process(
            fleet,
            casualties,
            currentPlan.template(),
            currentPlan.anchor()
        );

        Map<UUID, FormationMemberState> stateByParticipant = memberStates.stream()
            .collect(Collectors.toMap(
                FormationMemberState::participantId,
                Function.identity()
            ));

        List<FormationMemberState> survivingStates = activePlan.assignments().stream()
            .map(assignment -> stateByParticipant.get(assignment.participantId()))
            .filter(Objects::nonNull)
            .toList();

        FormationUpdate formationUpdate = fleetFormationCoordinator.update(
            fleet.fleetId(),
            activePlan.template().type(),
            activePlan.assignments(),
            survivingStates
        );

        List<FormationCombatDecision> decisions = combatStates.stream()
            .filter(state -> activePlan.assignments().stream()
                .anyMatch(assignment ->
                    assignment.participantId().equals(state.participantId())))
            .map(state -> decisionEngine.decide(
                state,
                policy,
                commanderUnderThreat
            ))
            .sorted(Comparator
                .comparingInt(FormationCombatDecision::priority)
                .reversed()
                .thenComparing(FormationCombatDecision::participantId))
            .toList();

        List<FormationProtectionOrder> protectionOrders =
            commanderUnderThreat && policy.protectCommander()
                ? protectionPlanner.plan(fleet, activePlan)
                : List.of();

        return new FormationLifecycleUpdate(
            activePlan,
            formationUpdate,
            decisions,
            protectionOrders
        );
    }
}
