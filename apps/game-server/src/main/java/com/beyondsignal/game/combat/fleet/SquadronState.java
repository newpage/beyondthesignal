package com.beyondsignal.game.combat.fleet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
public record SquadronState(UUID squadronId, String name, UUID leaderId, List<UUID> memberIds, String formation, FleetOrder currentOrder, UUID priorityTargetId, double morale, String status) {
  public SquadronState {
    squadronId = Objects.requireNonNull(squadronId);
    leaderId = Objects.requireNonNull(leaderId);
    memberIds = List.copyOf(memberIds);
    if (!memberIds.contains(leaderId)) throw new IllegalArgumentException("leader must be a squadron member");
    if (morale < 0 || morale > 1) throw new IllegalArgumentException("morale must be between 0 and 1");
  }
}
