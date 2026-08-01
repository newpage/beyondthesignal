package com.beyondsignal.game.combat.ai.tactical;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class TargetReservationTable {
    private final List<TargetReservation> reservations = new ArrayList<>();

    public synchronized void reserve(TargetReservation reservation) {
        Objects.requireNonNull(reservation, "reservation");
        reservations.removeIf(existing ->
            existing.attackerId().equals(reservation.attackerId())
        );
        reservations.add(reservation);
    }

    public synchronized int reservedDamage(UUID targetId) {
        return reservations.stream()
            .filter(reservation -> reservation.targetId().equals(targetId))
            .mapToInt(TargetReservation::reservedDamage)
            .sum();
    }

    public synchronized boolean attackerReserved(UUID attackerId) {
        return reservations.stream()
            .anyMatch(reservation -> reservation.attackerId().equals(attackerId));
    }

    public synchronized List<TargetReservation> reservations() {
        return List.copyOf(reservations);
    }

    public synchronized void clearForTarget(UUID targetId) {
        reservations.removeIf(reservation ->
            reservation.targetId().equals(targetId)
        );
    }
}
