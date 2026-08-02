package com.beyondsignal.game.combat.fleet.ai;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.Test;
class FleetDecisionTest {
 @Test void copiesThreatRanking(){
  var source=new ArrayList<ThreatScore>();
  source.add(new ThreatScore(UUID.randomUUID(),1,0,1));
  var d=new FleetDecision(UUID.randomUUID(),com.beyondsignal.game.combat.fleet.FleetDoctrine.AGGRESSIVE,
   FleetObjectiveType.ENGAGE_PRIMARY_THREAT,null,source,CommanderStatus.ACTIVE,false,1);
  source.clear();
  assertEquals(1,d.threats().size());
 }
}
