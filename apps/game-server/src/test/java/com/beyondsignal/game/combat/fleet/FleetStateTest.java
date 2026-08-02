package com.beyondsignal.game.combat.fleet;
import static org.junit.jupiter.api.Assertions.*;
import com.beyondsignal.game.combat.model.CombatSide;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
class FleetStateTest {
 @Test void preservesMembership() {
  UUID leader=UUID.randomUUID();
  SquadronState squadron=new SquadronState(UUID.randomUUID(),"Alpha",leader,List.of(leader),"WEDGE",null,null,1.0,"READY");
  FleetState fleet=new FleetState(UUID.randomUUID(),"Seventh Fleet","Admiral",CombatSide.FRIENDLY,FleetDoctrine.DEFENSIVE,List.of(squadron),List.of());
  assertEquals(leader,fleet.squadrons().getFirst().leaderId());
 }
}
