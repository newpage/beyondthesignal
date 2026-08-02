package com.beyondsignal.game.combat.fleet.ai;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
class FleetThreatAnalyzerTest {
 @Test void doctrineProfilesAreDeterministic(){
  assertTrue(DoctrineProfile.forDoctrine(com.beyondsignal.game.combat.fleet.FleetDoctrine.AGGRESSIVE).firepowerWeight()
   > DoctrineProfile.forDoctrine(com.beyondsignal.game.combat.fleet.FleetDoctrine.DEFENSIVE).firepowerWeight());
 }
}
