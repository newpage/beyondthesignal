package com.beyondsignal.game.websocket;

import com.beyondsignal.game.service.event.SessionEvent;
import com.beyondsignal.game.service.event.SessionEventType;
import com.beyondsignal.game.simulation.ShipState;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReplicationMessageSerializerTest {
    private final ReplicationMessageSerializer serializer = new ReplicationMessageSerializer();

    @Test
    void serializesVersionedShipStateUpdate() {
        UUID sessionId = UUID.randomUUID();
        Map<String, Object> payload = new java.util.LinkedHashMap<>();
        payload.put("tick", 42L);
        payload.put("position", Map.of("x", 1.0, "y", 2.0, "z", 3.0));
        payload.put("velocity", Map.of("x", 4.0, "y", 5.0, "z", 6.0));
        payload.put("headingDegrees", 90.0);
        payload.put("throttle", 75);
        payload.put("shieldsRaised", true);
        payload.put("weaponCooldownTicks", 6);
        payload.put("shotsFired", 12L);
        payload.put("combatContacts", java.util.List.of(Map.of(
            "id", "d4d0be92-df30-49c8-b642-f9f3c901ec46",
            "displayName", "Training Drone",
            "shieldStrength", 15,
            "hullIntegrity", 100,
            "destroyed", false
        )));
        payload.put("lastCombatEvent", Map.of(
            "sequence", 12L,
            "targetId", "d4d0be92-df30-49c8-b642-f9f3c901ec46",
            "weapon", "PHASER",
            "shieldDamage", 25,
            "hullDamage", 0,
            "targetDestroyed", false
        ));
        payload.put("selectedTargetId", "d4d0be92-df30-49c8-b642-f9f3c901ec46");

        SessionEvent event = new SessionEvent(
            sessionId,
            SessionEventType.SHIP_STATE_UPDATED,
            Instant.parse("2026-07-30T00:00:00Z"),
            Map.copyOf(payload)
        );


        JsonObject json = serializer.serialize(event);

        assertThat(json.getInteger("protocolVersion")).isEqualTo(1);
        assertThat(json.getString("type")).isEqualTo("SHIP_STATE_UPDATED");
        assertThat(json.getString("sessionId")).isEqualTo(sessionId.toString());
        assertThat(json.getLong("tick")).isEqualTo(42L);
        assertThat(json.getLong("snapshotVersion")).isEqualTo(42L);
        assertThat(json.getJsonObject("position").getDouble("x")).isEqualTo(1.0);
        assertThat(json.getInteger("throttle")).isEqualTo(75);
        assertThat(json.getBoolean("shieldsRaised")).isTrue();
        assertThat(json.getInteger("weaponCooldownTicks")).isEqualTo(6);
        assertThat(json.getLong("shotsFired")).isEqualTo(12L);
        assertThat(json.getJsonArray("combatContacts").size()).isEqualTo(1);
        assertThat(json.getJsonObject("lastCombatEvent").getInteger("shieldDamage")).isEqualTo(25);
        assertThat(json.getString("selectedTargetId"))
            .isEqualTo("d4d0be92-df30-49c8-b642-f9f3c901ec46");
    }

    @Test
    void serializesReconnectSnapshotUsingCurrentTickAsVersion() {
        ShipState state = ShipState.initial(UUID.randomUUID());

        JsonObject json = serializer.serializeSnapshot(state);

        assertThat(json.getString("type")).isEqualTo("SHIP_STATE_SNAPSHOT");
        assertThat(json.getLong("tick")).isZero();
        assertThat(json.getLong("snapshotVersion")).isZero();
        assertThat(json.getBoolean("shieldsRaised")).isFalse();
        assertThat(json.getInteger("weaponCooldownTicks")).isZero();
        assertThat(json.getLong("shotsFired")).isZero();
        assertThat(json.getJsonArray("combatContacts").size()).isEqualTo(1);
        assertThat(json.getJsonObject("lastCombatEvent")).isNull();
        assertThat(json.getString("selectedTargetId")).isNull();
    }
}
