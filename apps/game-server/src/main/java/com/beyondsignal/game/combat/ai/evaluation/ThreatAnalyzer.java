package com.beyondsignal.game.combat.ai.evaluation;

import com.beyondsignal.game.combat.ai.sensor.CombatSensorSuite;
import com.beyondsignal.game.combat.ai.snapshot.CombatAiSnapshot;
import com.beyondsignal.game.combat.ai.snapshot.CombatantSnapshot;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class ThreatAnalyzer {
    private final CombatSensorSuite sensors;

    public ThreatAnalyzer() {
        this(new CombatSensorSuite());
    }

    public ThreatAnalyzer(CombatSensorSuite sensors) {
        this.sensors = Objects.requireNonNull(sensors, "sensors");
    }

    public ThreatTable analyze(CombatAiSnapshot snapshot, UUID observerId) {
        CombatantSnapshot observer = sensors.self(snapshot, observerId);

        return new ThreatTable(
            observerId,
            sensors.hostiles(snapshot, observerId).stream()
                .map(hostile -> score(observer, hostile))
                .toList()
        );
    }

    private ThreatScore score(
        CombatantSnapshot observer,
        CombatantSnapshot hostile
    ) {
        Map<String, Integer> factors = new LinkedHashMap<>();

        factors.put("readyWeaponDamage", Math.min(40, hostile.readyWeaponDamage()));
        factors.put("weaponReadiness", (int) Math.min(20, hostile.readyWeaponCount() * 5));
        factors.put("hullDurability", (int) Math.round(hostile.hullPercentage() * 15));
        factors.put("shieldDurability",
            (int) Math.round(hostile.shields().percentage() * 10));
        factors.put("targetingObserver",
            hostile.selectedTarget()
                .filter(observer.participantId()::equals)
                .map(ignored -> 25)
                .orElse(0));
        factors.put("targetingQuality",
            (int) Math.round(hostile.targetingQuality() * 10));

        int total = factors.values().stream().mapToInt(Integer::intValue).sum();
        return new ThreatScore(hostile.participantId(), total, factors);
    }
}
