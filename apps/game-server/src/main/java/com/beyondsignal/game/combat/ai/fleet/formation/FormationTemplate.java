package com.beyondsignal.game.combat.ai.fleet.formation;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public record FormationTemplate(
    FormationType type,
    double spacing,
    List<FormationSlot> slots
) {
    public FormationTemplate {
        type = Objects.requireNonNull(type, "type");
        if (!Double.isFinite(spacing) || spacing <= 0.0) {
            throw new IllegalArgumentException("spacing must be positive");
        }
        slots = List.copyOf(Objects.requireNonNull(slots, "slots"));
        if (slots.isEmpty()) {
            throw new IllegalArgumentException("Formation requires at least one slot");
        }

        Set<Integer> indexes = new HashSet<>();
        Set<String> names = new HashSet<>();
        for (FormationSlot slot : slots) {
            if (!indexes.add(slot.index())) {
                throw new IllegalArgumentException(
                    "Duplicate formation slot index: " + slot.index()
                );
            }
            if (!names.add(slot.name())) {
                throw new IllegalArgumentException(
                    "Duplicate formation slot name: " + slot.name()
                );
            }
        }
    }

    public FormationSlot slot(int index) {
        return slots.stream()
            .filter(slot -> slot.index() == index)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Unknown formation slot index: " + index
            ));
    }
}
