package com.beyondsignal.game.simulation.runtime;

import com.beyondsignal.game.service.event.SessionEvent;
import com.beyondsignal.game.service.event.SessionEventPublisher;
import com.beyondsignal.game.service.event.SessionEventType;
import com.beyondsignal.game.simulation.ShipState;
import com.beyondsignal.game.simulation.CombatContactState;

import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.Objects;

public final class SimulationStateEventPublisher implements SimulationStateListener {
    private final SessionEventPublisher publisher;
    private final Clock clock;

    public SimulationStateEventPublisher(SessionEventPublisher publisher, Clock clock) {
        this.publisher = Objects.requireNonNull(publisher, "publisher must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    @Override
    public void onStateAdvanced(ShipState state) {
        Objects.requireNonNull(state, "state must not be null");

        Map<String, Object> position = new LinkedHashMap<>();
        position.put("x", state.position().x());
        position.put("y", state.position().y());
        position.put("z", state.position().z());

        Map<String, Object> velocity = new LinkedHashMap<>();
        velocity.put("x", state.velocity().x());
        velocity.put("y", state.velocity().y());
        velocity.put("z", state.velocity().z());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("tick", state.tick());
        payload.put("position", Map.copyOf(position));
        payload.put("velocity", Map.copyOf(velocity));
        payload.put("headingDegrees", state.headingDegrees());
        payload.put("throttle", state.throttle());
        payload.put("shieldsRaised", state.shieldsRaised());
        payload.put("weaponCooldownTicks", state.weaponCooldownTicks());
        payload.put("shotsFired", state.shotsFired());
        payload.put("redAlert", state.redAlert());
        payload.put("sensorCooldownTicks", state.sensorCooldownTicks());
        payload.put("scansCompleted", state.scansCompleted());
        payload.put("shieldStrength", state.shieldStrength());
        payload.put("hullIntegrity", state.hullIntegrity());
        payload.put("enemyWeaponCooldownTicks", state.enemyWeaponCooldownTicks());
        payload.put("torpedoesRemaining", state.torpedoesRemaining());
        payload.put("enemyShotsFired", state.enemyShotsFired());
        payload.put("destroyed", state.destroyed());
        payload.put("subsystems", state.subsystems().entrySet().stream().collect(java.util.stream.Collectors.toMap(
            entry -> entry.getKey().name(),
            entry -> Map.of("health", entry.getValue().health(), "powerAllocation", entry.getValue().powerAllocation())
        )));
        payload.put("combatContacts", state.combatContacts().values().stream()
            .map(SimulationStateEventPublisher::contactPayload)
            .toList());
        payload.put("worldObjects", state.worldObjects().values().stream()
            .map(SimulationStateEventPublisher::worldObjectPayload)
            .toList());
        payload.put("mission", missionPayload(state));
        payload.put("crewAdvisory", Map.of(
            "sequence", state.crewAdvisory().sequence(),
            "station", state.crewAdvisory().station(),
            "severity", state.crewAdvisory().severity(),
            "message", state.crewAdvisory().message()
        ));
        if (state.lastCombatEvent() != null) {
            Map<String, Object> combatEvent = new LinkedHashMap<>();
            combatEvent.put("sequence", state.lastCombatEvent().sequence());
            combatEvent.put("targetId", state.lastCombatEvent().targetId().toString());
            combatEvent.put("weapon", state.lastCombatEvent().weapon());
            combatEvent.put("shieldDamage", state.lastCombatEvent().shieldDamage());
            combatEvent.put("hullDamage", state.lastCombatEvent().hullDamage());
            combatEvent.put("targetDestroyed", state.lastCombatEvent().targetDestroyed());
            payload.put("lastCombatEvent", Map.copyOf(combatEvent));
        }
        if (state.selectedTargetId() != null) {
            payload.put("selectedTargetId", state.selectedTargetId().toString());
        }

        publisher.publish(new SessionEvent(
            state.sessionId(),
            SessionEventType.SHIP_STATE_UPDATED,
            clock.instant(),
            Map.copyOf(payload)
        ));
    }

    private static Map<String, Object> worldObjectPayload(com.beyondsignal.game.simulation.WorldObjectState object) {
        return Map.of(
            "id", object.id().toString(),
            "displayName", object.displayName(),
            "type", object.type(),
            "position", Map.of("x", object.position().x(), "y", object.position().y(), "z", object.position().z()),
            "velocity", Map.of("x", object.velocity().x(), "y", object.velocity().y(), "z", object.velocity().z()),
            "hostile", object.hostile()
        );
    }

    private static Map<String, Object> missionPayload(ShipState state) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", state.mission().id().toString());
        payload.put("title", state.mission().title());
        payload.put("objective", state.mission().objective());
        payload.put("status", state.mission().status().name());
        payload.put("score", state.mission().score());
        payload.put("startedAtTick", state.mission().startedAtTick());
        if (state.mission().completedAtTick() != null) payload.put("completedAtTick", state.mission().completedAtTick());
        return Map.copyOf(payload);
    }

    private static Map<String, Object> contactPayload(CombatContactState contact) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", contact.id().toString());
        payload.put("displayName", contact.displayName());
        payload.put("shieldStrength", contact.shieldStrength());
        payload.put("hullIntegrity", contact.hullIntegrity());
        payload.put("destroyed", contact.destroyed());
        return Map.copyOf(payload);
    }
}
