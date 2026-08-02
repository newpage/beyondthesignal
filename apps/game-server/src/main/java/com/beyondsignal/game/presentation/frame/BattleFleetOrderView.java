package com.beyondsignal.game.presentation.frame;
import java.util.Map;
import java.util.UUID;
public record BattleFleetOrderView(UUID id, String type, int priority, UUID targetId, Map<String,String> parameters) {
  public BattleFleetOrderView { parameters = Map.copyOf(parameters); }
}
