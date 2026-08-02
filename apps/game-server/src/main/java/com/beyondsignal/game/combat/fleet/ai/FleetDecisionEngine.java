package com.beyondsignal.game.combat.fleet.ai;
import com.beyondsignal.game.combat.engine.*;
import com.beyondsignal.game.combat.fleet.*;
import java.util.*;
public final class FleetDecisionEngine {
 private final FleetThreatAnalyzer analyzer;
 public FleetDecisionEngine(){this(new FleetThreatAnalyzer());}
 public FleetDecisionEngine(FleetThreatAnalyzer analyzer){this.analyzer=Objects.requireNonNull(analyzer);}
 public FleetDecision decide(CombatEncounter encounter,FleetState fleet,long tick){
  var members=fleet.squadrons().stream().flatMap(s->s.memberIds().stream()).toList();
  long operational=members.stream().map(encounter::participant).flatMap(Optional::stream)
   .filter(CombatParticipant::operational).count();
  double ratio=members.isEmpty()?0:(double)operational/members.size();
  var threats=analyzer.analyze(encounter,fleet);
  boolean retreat=fleet.doctrine()==FleetDoctrine.RETREAT ||
   ratio < DoctrineProfile.forDoctrine(fleet.doctrine()).retreatThreshold();
  FleetObjectiveType objective=retreat?FleetObjectiveType.RETREAT:
   threats.isEmpty()?FleetObjectiveType.HOLD_POSITION:
   fleet.doctrine()==FleetDoctrine.DEFENSIVE||fleet.doctrine()==FleetDoctrine.ESCORT
    ?FleetObjectiveType.DEFEND_FLAGSHIP:FleetObjectiveType.ENGAGE_PRIMARY_THREAT;
  CommanderStatus status=operational==0?CommanderStatus.NO_COMBAT_CAPABILITY:
   retreat?CommanderStatus.WITHDRAWING:ratio<0.5?CommanderStatus.DEGRADED:CommanderStatus.ACTIVE;
  return new FleetDecision(fleet.fleetId(),fleet.doctrine(),objective,
   threats.isEmpty()?null:threats.getFirst().participantId(),threats,status,retreat,tick);
 }
}
