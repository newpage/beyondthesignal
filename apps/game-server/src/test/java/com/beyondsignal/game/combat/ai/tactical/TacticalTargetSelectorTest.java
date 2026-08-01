package com.beyondsignal.game.combat.ai.tactical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TacticalTargetSelectorTest {
    @Test
    void skipsFullyReservedTarget() {
        UUID preferred = UUID.randomUUID();
        UUID fallback = UUID.randomUUID();

        List<TacticalTargetProfile> targets = List.of(
            new TacticalTargetProfile(
                preferred,
                TargetPriorityClass.FLAGSHIP,
                0.5,
                0.5,
                30,
                true,
                true,
                false
            ),
            new TacticalTargetProfile(
                fallback,
                TargetPriorityClass.ESCORT,
                1.0,
                1.0,
                10,
                false,
                false,
                false
            )
        );

        TargetReservationTable reservations = new TargetReservationTable();
        reservations.reserve(
            new TargetReservation(UUID.randomUUID(), preferred, 120)
        );

        TacticalTargetScore selected = new TacticalTargetSelector().select(
            targets,
            reservations,
            TargetSelectionPolicy.standard()
        ).orElseThrow();

        assertEquals(fallback, selected.targetId());
    }
}
