package com.beyondsignal.game.combat.fleet.ai;
import java.util.UUID;
public record ThreatScore(UUID participantId, double score, int firepower, double hullRatio) {
 public ThreatScore {
  if (participantId == null || !Double.isFinite(score) || score < 0) throw new IllegalArgumentException();
 }
}
