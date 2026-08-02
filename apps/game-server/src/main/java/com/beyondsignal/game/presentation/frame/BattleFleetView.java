package com.beyondsignal.game.presentation.frame;
import java.util.List;
import java.util.UUID;
public record BattleFleetView(UUID id, String name, String commander, String faction, String doctrine, List<UUID> squadronIds, List<UUID> orderIds) {
  public BattleFleetView { squadronIds = List.copyOf(squadronIds); orderIds = List.copyOf(orderIds); }
}
