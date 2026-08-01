package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record FormationState(
    UUID fleetId,
    FormationType type,
    FormationStatus status,
    List<FormationAssignment> assignments,
    List<FormationMemberState> members,
    double integrity
) {
    public FormationState {
        fleetId = Objects.requireNonNull(fleetId, "fleetId");
        type = Objects.requireNonNull(type, "type");
        status = Objects.requireNonNull(status, "status");
        assignments = List.copyOf(Objects.requireNonNull(assignments, "assignments"));
        members = List.copyOf(Objects.requireNonNull(members, "members"));
        if (!Double.isFinite(integrity) || integrity < 0.0 || integrity > 1.0) {
            throw new IllegalArgumentException("integrity must be between 0 and 1");
        }
    }

    public Optional<FormationAssignment> assignmentFor(UUID participantId) {
        return assignments.stream()
            .filter(assignment -> assignment.participantId().equals(participantId))
            .findFirst();
    }

    public long membersInPosition() {
        return members.stream()
            .filter(FormationMemberState::inPosition)
            .count();
    }
}
