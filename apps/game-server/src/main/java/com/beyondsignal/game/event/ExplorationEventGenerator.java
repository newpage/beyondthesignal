package com.beyondsignal.game.event;
import com.beyondsignal.game.galaxy.StarSystem; import java.util.*;
public final class ExplorationEventGenerator{
 public ExplorationEvent generate(StarSystem s,long tick,long seed){ Objects.requireNonNull(s); if(tick<0) throw new IllegalArgumentException("tick cannot be negative"); Random r=new Random(seed^s.id().hashCode()^tick); ExplorationEventType t=choose(s,r); return switch(t){
 case SPATIAL_ANOMALY->ExplorationEvent.pending(id(s,tick,t),t,"Unstable Spatial Distortion","Sensors detect a fluctuating subspace distortion near "+s.name()+".",s.id(),tick,List.of(ExplorationChoice.INVESTIGATE,ExplorationChoice.EVADE,ExplorationChoice.IGNORE));
 case DISTRESS_CALL->ExplorationEvent.pending(id(s,tick,t),t,"Distress Signal","A civilian vessel is transmitting a weak emergency beacon.",s.id(),tick,List.of(ExplorationChoice.ASSIST,ExplorationChoice.HAIL,ExplorationChoice.IGNORE));
 case HOSTILE_AMBUSH->ExplorationEvent.pending(id(s,tick,t),t,"Hostile Contact","An unidentified armed vessel emerges from sensor shadow.",s.id(),tick,List.of(ExplorationChoice.ENGAGE,ExplorationChoice.EVADE,ExplorationChoice.HAIL));
 case DERELICT_VESSEL->ExplorationEvent.pending(id(s,tick,t),t,"Derelict Vessel","A powerless vessel is drifting without life signs.",s.id(),tick,List.of(ExplorationChoice.INVESTIGATE,ExplorationChoice.SALVAGE,ExplorationChoice.IGNORE));
 case DIPLOMATIC_CONTACT->ExplorationEvent.pending(id(s,tick,t),t,"First Contact Opportunity","A previously unknown vessel is requesting formal communication.",s.id(),tick,List.of(ExplorationChoice.HAIL,ExplorationChoice.EVADE,ExplorationChoice.IGNORE));};}
 private static ExplorationEventType choose(StarSystem s,Random r){ if(s.hostileTerritory()&&r.nextDouble()<.65) return ExplorationEventType.HOSTILE_AMBUSH; if(s.hasStation()&&r.nextDouble()<.4) return ExplorationEventType.DISTRESS_CALL; ExplorationEventType[] v=ExplorationEventType.values(); return v[r.nextInt(v.length)];}
 private static String id(StarSystem s,long tick,ExplorationEventType t){return s.id()+"-"+tick+"-"+t.name().toLowerCase();}
}
