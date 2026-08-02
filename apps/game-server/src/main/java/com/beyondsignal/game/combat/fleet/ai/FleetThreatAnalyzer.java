package com.beyondsignal.game.combat.fleet.ai;
import com.beyondsignal.game.combat.engine.*;
import com.beyondsignal.game.combat.fleet.FleetState;
import java.util.*;
public final class FleetThreatAnalyzer {
 public List<ThreatScore> analyze(CombatEncounter encounter, FleetState fleet) {
  DoctrineProfile p=DoctrineProfile.forDoctrine(fleet.doctrine());
  return encounter.participants().stream()
   .filter(CombatParticipant::operational)
   .filter(x -> x.side()!=fleet.side())
   .map(x -> score(x,p))
   .sorted(Comparator.comparingDouble(ThreatScore::score).reversed()
    .thenComparing(x -> x.participantId().toString()))
   .toList();
 }
 private ThreatScore score(CombatParticipant x, DoctrineProfile p) {
  int firepower=x.weapons().stream().filter(w -> w.readyToFire())
   .mapToInt(w -> w.definition().baseDamage()).sum();
  double hull=(double)x.hull()/x.maximumHull();
  double value=firepower*p.firepowerWeight()+hull*100*p.durabilityWeight()
   +x.targetingQuality()*50*p.accuracyWeight();
  return new ThreatScore(x.participantId(), value, firepower, hull);
 }
}
