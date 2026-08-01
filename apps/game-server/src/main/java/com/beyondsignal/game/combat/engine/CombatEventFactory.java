package com.beyondsignal.game.combat.engine;

import com.beyondsignal.game.combat.event.CombatEvent;
import com.beyondsignal.game.combat.event.CombatEventType;
import java.time.Instant;
import java.util.Map;

public final class CombatEventFactory {
    public CombatEvent targetSelected(
        CombatEncounter encounter,
        CombatParticipant actor,
        CombatParticipant target
    ) {
        return event(
            encounter,
            actor,
            target,
            CombatEventType.TARGET_SELECTED,
            Map.of("targetId", target.participantId().toString())
        );
    }

    public CombatEvent weaponFired(
        CombatEncounter encounter,
        FireSolution solution
    ) {
        return new CombatEvent(
            encounter.combatId(),
            encounter.nextEventSequence(),
            encounter.context().clock().currentTick(),
            solution.attackerId(),
            solution.targetId(),
            CombatEventType.WEAPON_FIRED,
            Instant.EPOCH.plusMillis(encounter.context().clock().currentTick()),
            Map.of(
                "weaponMountId", solution.weapon().mountId().toString(),
                "weaponId", solution.weapon().definition().id(),
                "distance", Double.toString(solution.distance()),
                "hitProbability", Double.toString(solution.hitProbability()),
                "roll", Double.toString(solution.roll())
            )
        );
    }

    public CombatEvent weaponResult(
        CombatEncounter encounter,
        FireSolution solution
    ) {
        return new CombatEvent(
            encounter.combatId(),
            encounter.nextEventSequence(),
            encounter.context().clock().currentTick(),
            solution.attackerId(),
            solution.targetId(),
            solution.hit() ? CombatEventType.WEAPON_HIT : CombatEventType.WEAPON_MISSED,
            Instant.EPOCH.plusMillis(encounter.context().clock().currentTick()),
            Map.of(
                "weaponMountId", solution.weapon().mountId().toString(),
                "weaponId", solution.weapon().definition().id()
            )
        );
    }

    private CombatEvent event(
        CombatEncounter encounter,
        CombatParticipant source,
        CombatParticipant target,
        CombatEventType type,
        Map<String, String> payload
    ) {
        return new CombatEvent(
            encounter.combatId(),
            encounter.nextEventSequence(),
            encounter.context().clock().currentTick(),
            source.participantId(),
            target.participantId(),
            type,
            Instant.EPOCH.plusMillis(encounter.context().clock().currentTick()),
            payload
        );
    }
}
