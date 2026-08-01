package com.beyondsignal.game.combat.ai.fleet.formation;

public record FormationMetrics(
    int assignedMembers,
    int membersInPosition,
    double averageDrift,
    double maximumDrift,
    double integrity
) {
    public FormationMetrics {
        if (assignedMembers < 0 || membersInPosition < 0) {
            throw new IllegalArgumentException("Member counts cannot be negative");
        }
        if (membersInPosition > assignedMembers) {
            throw new IllegalArgumentException(
                "membersInPosition cannot exceed assignedMembers"
            );
        }
        if (!Double.isFinite(averageDrift) || averageDrift < 0.0) {
            throw new IllegalArgumentException("averageDrift must be non-negative");
        }
        if (!Double.isFinite(maximumDrift) || maximumDrift < 0.0) {
            throw new IllegalArgumentException("maximumDrift must be non-negative");
        }
        if (!Double.isFinite(integrity) || integrity < 0.0 || integrity > 1.0) {
            throw new IllegalArgumentException("integrity must be between 0 and 1");
        }
    }
}
