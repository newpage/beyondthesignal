package com.beyondsignal.game.combat.ai.tactical;

import java.util.List;
import java.util.Objects;

public record FocusFirePlan(
    List<TargetAssignment> assignments,
    TargetReservationTable reservations
) {
    public FocusFirePlan {
        assignments = List.copyOf(Objects.requireNonNull(assignments, "assignments"));
        reservations = Objects.requireNonNull(reservations, "reservations");
    }
}
