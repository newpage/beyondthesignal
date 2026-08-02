package com.beyondsignal.game.combat.fleet;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
public record FleetOrder(UUID orderId, FleetOrderType type, int priority, UUID targetId, Map<String,String> parameters) {
  public FleetOrder {
    orderId = Objects.requireNonNull(orderId);
    type = Objects.requireNonNull(type);
    if (priority < 0) throw new IllegalArgumentException("priority cannot be negative");
    parameters = Map.copyOf(parameters);
  }
}
