package com.beyondsignal.game.combat.ai.fleet.formation;

import com.beyondsignal.game.combat.ai.fleet.FleetMember;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class FormationAllocator {
    public List<FormationAssignment> allocate(
        List<FleetMember> members,
        FormationTemplate template,
        FormationAnchor anchor
    ) {
        Objects.requireNonNull(members, "members");
        Objects.requireNonNull(template, "template");
        Objects.requireNonNull(anchor, "anchor");

        if (members.size() > template.slots().size()) {
            throw new IllegalArgumentException(
                "Formation template does not contain enough slots"
            );
        }

        List<FleetMember> unassigned = new ArrayList<>(members);
        unassigned.sort(Comparator
            .comparingInt(FleetMember::commandPriority)
            .reversed()
            .thenComparing(FleetMember::participantId));

        List<FormationSlot> availableSlots = new ArrayList<>(template.slots());
        availableSlots.sort(Comparator
            .comparingInt(FormationSlot::priority)
            .reversed()
            .thenComparingInt(FormationSlot::index));

        List<FormationAssignment> assignments = new ArrayList<>();
        Set<Integer> usedSlots = new HashSet<>();

        // First pass favors role-compatible slots.
        for (FleetMember member : new ArrayList<>(unassigned)) {
            FormationSlot slot = availableSlots.stream()
                .filter(candidate -> !usedSlots.contains(candidate.index()))
                .filter(candidate -> candidate.preferredRole() == member.role())
                .findFirst()
                .orElse(null);

            if (slot != null) {
                assignments.add(assignment(member, slot, template, anchor));
                usedSlots.add(slot.index());
                unassigned.remove(member);
            }
        }

        // Second pass assigns remaining members deterministically.
        for (FleetMember member : unassigned) {
            FormationSlot slot = availableSlots.stream()
                .filter(candidate -> !usedSlots.contains(candidate.index()))
                .findFirst()
                .orElseThrow();
            assignments.add(assignment(member, slot, template, anchor));
            usedSlots.add(slot.index());
        }

        assignments.sort(Comparator
            .comparingInt((FormationAssignment value) -> value.slot().index())
            .thenComparing(FormationAssignment::participantId));

        return List.copyOf(assignments);
    }

    private static FormationAssignment assignment(
        FleetMember member,
        FormationSlot slot,
        FormationTemplate template,
        FormationAnchor anchor
    ) {
        return new FormationAssignment(
            member.participantId(),
            slot,
            anchor.worldPosition(slot, template.spacing())
        );
    }
}
