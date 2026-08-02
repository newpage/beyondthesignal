package com.beyondsignal.game.presentation.frame;
import java.util.List;
import java.util.UUID;
public record BattleSquadronView(UUID id, String name, UUID leaderId, List<UUID> memberIds, String formation, UUID currentOrderId, UUID priorityTargetId, double morale, String status) {
  public BattleSquadronView { memberIds = List.copyOf(memberIds); }
}
