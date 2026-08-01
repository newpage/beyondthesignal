package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.Objects;
import java.util.UUID;

public record FormationAnchor(
    UUID leaderId,
    FormationVector position,
    FormationVector forward,
    FormationVector right,
    FormationVector up
) {
    public FormationAnchor {
        leaderId = Objects.requireNonNull(leaderId, "leaderId");
        position = Objects.requireNonNull(position, "position");
        forward = Objects.requireNonNull(forward, "forward");
        right = Objects.requireNonNull(right, "right");
        up = Objects.requireNonNull(up, "up");
    }

    public FormationVector worldPosition(FormationSlot slot, double spacing) {
        Objects.requireNonNull(slot, "slot");
        if (!Double.isFinite(spacing) || spacing <= 0.0) {
            throw new IllegalArgumentException("spacing must be positive");
        }

        FormationVector offset = slot.offset();
        return position
            .add(right.scale(offset.x() * spacing))
            .add(up.scale(offset.y() * spacing))
            .add(forward.scale(offset.z() * spacing));
    }
}
