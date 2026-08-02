package com.beyondsignal.game.combat.fleet.ai;
import com.beyondsignal.game.combat.fleet.FleetDoctrine;
import java.util.*;
public record FleetDecision(UUID fleetId,FleetDoctrine doctrine,FleetObjectiveType objective,
 UUID primaryTargetId,List<ThreatScore> threats,CommanderStatus commanderStatus,boolean retreat,long generatedTick) {
 public FleetDecision { threats=List.copyOf(threats); }
}
