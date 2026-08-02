package com.beyondsignal.game.presentation.frame;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record BattleFormationView(
    UUID id,
    String name,
    UUID leaderId,
    List<UUID> memberIds,
    double integrity
) {
    public BattleFormationView {
        id = Objects.requireNonNull(id, "id");
        name = Objects.requireNonNull(name, "name");
        leaderId = Objects.requireNonNull(leaderId, "leaderId");
        memberIds = List.copyOf(memberIds);
        if (!Double.isFinite(integrity) || integrity < 0.0 || integrity > 1.0) {
            throw new IllegalArgumentException("integrity must be between 0 and 1");
        }
    }
}
