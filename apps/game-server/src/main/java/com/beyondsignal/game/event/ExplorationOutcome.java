package com.beyondsignal.game.event;
public record ExplorationOutcome(int scoreDelta,int reputationDelta,int hullDelta,int resourceDelta,String narrative){ public ExplorationOutcome{ if(narrative==null||narrative.isBlank()) throw new IllegalArgumentException("narrative is required"); }}
