package com.beyondsignal.game.bridge;

import com.beyondsignal.game.exploration.ExplorationCampaignState;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record BridgeState(
    long version,
    AlertStatus alertStatus,
    double headingDegrees,
    int throttle,
    boolean shieldsRaised,
    int shieldStrength,
    int hullIntegrity,
    String selectedTargetId,
    int torpedoesRemaining,
    Map<Subsystem, Integer> powerAllocations,
    Map<BridgeStation, UUID> crewAssignments,
    ExplorationCampaignState campaign,
    List<BridgeMessage> messages
) {
    public BridgeState {
        if (version < 0) {
            throw new IllegalArgumentException("version cannot be negative");
        }
        if (!Double.isFinite(headingDegrees) || headingDegrees < 0 || headingDegrees >= 360) {
            throw new IllegalArgumentException("headingDegrees must be between 0 and 360");
        }
        if (throttle < 0 || throttle > 100) {
            throw new IllegalArgumentException("throttle must be between 0 and 100");
        }
        if (shieldStrength < 0 || shieldStrength > 100) {
            throw new IllegalArgumentException("shieldStrength must be between 0 and 100");
        }
        if (hullIntegrity < 0 || hullIntegrity > 100) {
            throw new IllegalArgumentException("hullIntegrity must be between 0 and 100");
        }
        if (torpedoesRemaining < 0) {
            throw new IllegalArgumentException("torpedoesRemaining cannot be negative");
        }
        powerAllocations = Map.copyOf(powerAllocations);
        crewAssignments = Map.copyOf(crewAssignments);
        messages = List.copyOf(messages);
    }
}
