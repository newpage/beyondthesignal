package com.beyondsignal.game.combat.fleet;
import com.beyondsignal.game.combat.model.CombatSide;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
public record FleetState(UUID fleetId, String name, String commander, CombatSide side, FleetDoctrine doctrine, List<SquadronState> squadrons, List<FleetOrder> orders) {
  public FleetState {
    fleetId = Objects.requireNonNull(fleetId);
    side = Objects.requireNonNull(side);
    doctrine = Objects.requireNonNull(doctrine);
    squadrons = List.copyOf(squadrons);
    orders = List.copyOf(orders);
  }
}
