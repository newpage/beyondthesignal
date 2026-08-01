package com.beyondsignal.game.combat.ai.fleet;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record FleetDefinition(
    UUID fleetId,
    FleetDoctrine doctrine,
    List<FleetMember> members
) {
    public FleetDefinition {
        fleetId = Objects.requireNonNull(fleetId, "fleetId");
        doctrine = Objects.requireNonNull(doctrine, "doctrine");
        members = List.copyOf(Objects.requireNonNull(members, "members"));
        if (members.isEmpty()) {
            throw new IllegalArgumentException("Fleet requires at least one member");
        }
        long commanders = members.stream()
            .filter(member -> member.role() == FleetRole.COMMANDER)
            .count();
        if (commanders > 1) {
            throw new IllegalArgumentException("Fleet may have at most one commander");
        }
    }

    public Optional<FleetMember> commander() {
        return members.stream()
            .filter(member -> member.role() == FleetRole.COMMANDER)
            .findFirst();
    }
}
