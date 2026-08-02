package com.beyondsignal.game.combat.fleet.ai;
import com.beyondsignal.game.combat.engine.CombatEncounter;
import java.util.*;
public final class FleetCommanderAI {
 private final FleetDecisionEngine engine;
 public FleetCommanderAI(){this(new FleetDecisionEngine());}
 public FleetCommanderAI(FleetDecisionEngine engine){this.engine=Objects.requireNonNull(engine);}
 public List<FleetDecision> evaluate(CombatEncounter encounter,long tick){
  return encounter.fleets().stream().sorted(Comparator.comparing(f->f.fleetId().toString()))
   .map(f->engine.decide(encounter,f,tick)).toList();
 }
}
