package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class FormationOrderGenerator {
    private final FormationCommandTranslator translator;

    public FormationOrderGenerator() {
        this(new FormationCommandTranslator());
    }

    public FormationOrderGenerator(FormationCommandTranslator translator) {
        this.translator = Objects.requireNonNull(translator, "translator");
    }

    public FormationOrderSet generate(
        FormationState state,
        FormationObjective objective
    ) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(objective, "objective");

        Map<UUID, FormationMemberState> membersById = state.members().stream()
            .collect(Collectors.toMap(
                FormationMemberState::participantId,
                Function.identity()
            ));

        List<FormationOrder> orders = state.assignments().stream()
            .map(assignment -> {
                FormationMemberState memberState = membersById.get(
                    assignment.participantId()
                );
                if (memberState == null) {
                    throw new IllegalArgumentException(
                        "Missing member state for assignment: "
                            + assignment.participantId()
                    );
                }

                return new FormationOrder(
                    assignment.participantId(),
                    objective,
                    assignment,
                    translator.translate(assignment, memberState, objective)
                );
            })
            .sorted(Comparator
                .comparingInt(
                    (FormationOrder order) -> order.movementIntent().urgency()
                )
                .reversed()
                .thenComparing(FormationOrder::participantId))
            .toList();

        return new FormationOrderSet(
            state.fleetId(),
            objective,
            orders
        );
    }
}
