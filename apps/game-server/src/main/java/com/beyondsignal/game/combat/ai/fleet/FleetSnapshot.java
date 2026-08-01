package com.beyondsignal.game.combat.ai.fleet;

import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.CombatantSnapshot;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record FleetSnapshot(
    UUID fleetId,
    FleetDoctrine doctrine,
    List<FleetMember> members,
    CombatAiSnapshot combat
) {
    public FleetSnapshot {
        fleetId = Objects.requireNonNull(fleetId, "fleetId");
        doctrine = Objects.requireNonNull(doctrine, "doctrine");
        members = List.copyOf(Objects.requireNonNull(members, "members"));
        combat = Objects.requireNonNull(combat, "combat");
    }

    public List<CombatantSnapshot> activeMembers() {
        return members.stream()
            .map(FleetMember::participantId)
            .map(combat::combatant)
            .flatMap(Optional::stream)
            .filter(snapshot -> snapshot.status() !=
                com.beyondsignal.game.combat.engine.CombatParticipantStatus.DESTROYED)
            .toList();
    }
}
