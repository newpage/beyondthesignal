package com.beyondsignal.game.combat.ai.fleet.formation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.beyondsignal.game.combat.ai.fleet.FleetDefinition;
import com.beyondsignal.game.combat.ai.fleet.FleetDoctrine;
import com.beyondsignal.game.combat.ai.fleet.FleetMember;
import com.beyondsignal.game.combat.ai.fleet.FleetRole;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FormationScaleIntegrationTest {
    @Test
    void coordinatesTenShipFleetDeterministically() {
        UUID commander = UUID.fromString(
            "00000000-0000-0000-0000-000000000100"
        );
        List<FleetMember> members = new ArrayList<>();
        members.add(new FleetMember(commander, FleetRole.COMMANDER, 100));

        for (int index = 1; index < 10; index++) {
            FleetRole role = index <= 4
                ? FleetRole.ESCORT
                : index <= 7
                    ? FleetRole.STRIKE
                    : FleetRole.SUPPORT;
            members.add(new FleetMember(
                UUID.nameUUIDFromBytes(("member-" + index).getBytes()),
                role,
                100 - index
            ));
        }

        FleetDefinition fleet = new FleetDefinition(
            UUID.randomUUID(),
            FleetDoctrine.FOCUS_FIRE,
            members
        );

        FormationTemplate template = largeTemplate();
        FormationPlan first = new FormationPlanner().plan(
            fleet,
            template,
            anchor(commander)
        );
        FormationPlan second = new FormationPlanner().plan(
            fleet,
            template,
            anchor(commander)
        );

        assertEquals(first.assignments(), second.assignments());
        assertEquals(10, first.assignments().size());

        List<FormationMemberState> states = first.assignments().stream()
            .map(value -> new FormationMemberState(
                value.participantId(),
                value.desiredPosition(),
                value.desiredPosition(),
                10.0
            ))
            .toList();

        FormationState state = new FormationCoordinator().evaluate(
            fleet.fleetId(),
            template.type(),
            first.assignments(),
            states
        );

        assertEquals(FormationStatus.STABLE, state.status());
        assertTrue(state.integrity() >= 0.85);
    }

    private static FormationTemplate largeTemplate() {
        List<FormationSlot> slots = new ArrayList<>();
        for (int index = 0; index < 10; index++) {
            slots.add(new FormationSlot(
                index,
                "slot-" + index,
                new FormationVector(index - 5, 0, -(index / 2.0)),
                index == 0
                    ? FleetRole.COMMANDER
                    : index <= 4
                        ? FleetRole.ESCORT
                        : index <= 7
                            ? FleetRole.STRIKE
                            : FleetRole.SUPPORT,
                100 - index
            ));
        }
        return new FormationTemplate(
            FormationType.SCREEN,
            100.0,
            slots
        );
    }

    private static FormationAnchor anchor(UUID commander) {
        return new FormationAnchor(
            commander,
            FormationVector.zero(),
            new FormationVector(0, 0, 1),
            new FormationVector(1, 0, 0),
            new FormationVector(0, 1, 0)
        );
    }
}
