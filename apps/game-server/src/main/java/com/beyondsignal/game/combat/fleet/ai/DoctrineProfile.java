package com.beyondsignal.game.combat.fleet.ai;
import com.beyondsignal.game.combat.fleet.FleetDoctrine;
public record DoctrineProfile(double firepowerWeight,double durabilityWeight,double accuracyWeight,double retreatThreshold) {
 public static DoctrineProfile forDoctrine(FleetDoctrine d) {
  return switch(d) {
   case AGGRESSIVE, AMBUSH -> new DoctrineProfile(1.45,0.65,1.15,0.15);
   case DEFENSIVE, ESCORT -> new DoctrineProfile(0.90,1.35,1.00,0.35);
   case RETREAT -> new DoctrineProfile(0.50,0.50,0.50,0.80);
   case PATROL, SCOUT -> new DoctrineProfile(1.00,0.90,1.20,0.25);
  };
 }
}
