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

public CombatEvent beamFired(
    CombatEncounter encounter,
    FireSolution solution
) {
    return event(
        encounter,
        encounter.participant(solution.attackerId()).orElseThrow(),
        encounter.participant(solution.targetId()).orElseThrow(),
        CombatEventType.BEAM_FIRED,
        Map.of(
            "weaponMountId", solution.weapon().mountId().toString(),
            "weaponId", solution.weapon().definition().id(),
            "damageType", solution.weapon().definition().damageType().name(),
            "baseDamage", Integer.toString(
                solution.weapon().definition().baseDamage()
            )
        )
    );
}

public CombatEvent beamHit(
    CombatEncounter encounter,
    FireSolution solution,
    BeamDamageResolution resolution
) {
    return event(
        encounter,
        encounter.participant(solution.attackerId()).orElseThrow(),
        encounter.participant(solution.targetId()).orElseThrow(),
        CombatEventType.BEAM_HIT,
        Map.of(
            "weaponId", solution.weapon().definition().id(),
            "quadrant", resolution.shieldImpact().quadrant().name()
        )
    );
}

public CombatEvent shieldImpact(
    CombatEncounter encounter,
    FireSolution solution,
    BeamDamageResolution resolution
) {
    var impact = resolution.shieldImpact();
    return event(
        encounter,
        encounter.participant(solution.attackerId()).orElseThrow(),
        encounter.participant(solution.targetId()).orElseThrow(),
        CombatEventType.SHIELD_IMPACT,
        Map.of(
            "quadrant", impact.quadrant().name(),
            "damageType", impact.damageType().name(),
            "incomingDamage", Integer.toString(impact.incomingDamage()),
            "absorbedDamage", Integer.toString(impact.absorbedDamage()),
            "penetratingDamage", Integer.toString(impact.penetratingDamage())
        )
    );
}

public CombatEvent shieldCollapsed(
    CombatEncounter encounter,
    FireSolution solution,
    BeamDamageResolution resolution
) {
    return event(
        encounter,
        encounter.participant(solution.attackerId()).orElseThrow(),
        encounter.participant(solution.targetId()).orElseThrow(),
        CombatEventType.SHIELD_COLLAPSED,
        Map.of("quadrant", resolution.shieldImpact().quadrant().name())
    );
}

public CombatEvent hullDamage(
    CombatEncounter encounter,
    FireSolution solution,
    BeamDamageResolution resolution
) {
    return event(
        encounter,
        encounter.participant(solution.attackerId()).orElseThrow(),
        encounter.participant(solution.targetId()).orElseThrow(),
        CombatEventType.HULL_DAMAGE,
        Map.of(
            "damage", Integer.toString(resolution.hullDamage()),
            "hullRemaining", Integer.toString(resolution.hullRemaining())
        )
    );
}

public CombatEvent shipDestroyed(
    CombatEncounter encounter,
    FireSolution solution
) {
    return event(
        encounter,
        encounter.participant(solution.attackerId()).orElseThrow(),
        encounter.participant(solution.targetId()).orElseThrow(),
        CombatEventType.SHIP_DESTROYED,
        Map.of("weaponId", solution.weapon().definition().id())
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
