package com.beyondsignal.game.websocket;

import com.beyondsignal.game.service.event.SessionEvent;
import com.beyondsignal.game.service.event.SessionEventType;
import com.beyondsignal.game.simulation.ShipState;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.Objects;

public final class ReplicationMessageSerializer {
    public static final int PROTOCOL_VERSION = 1;

    public JsonObject serialize(SessionEvent event) {
        Objects.requireNonNull(event, "event must not be null");

        if (event.type() == SessionEventType.SHIP_STATE_UPDATED) {
            Map<String, Object> payload = event.payload();
            long tick = requiredLong(payload, "tick");

            return envelope(event.type().name(), event.sessionId().toString())
                .put("occurredAt", event.occurredAt().toString())
                .put("tick", tick)
                .put("snapshotVersion", tick)
                .put("position", new JsonObject(requiredMap(payload, "position")))
                .put("velocity", new JsonObject(requiredMap(payload, "velocity")))
                .put("headingDegrees", requiredNumber(payload, "headingDegrees").doubleValue())
                .put("throttle", requiredNumber(payload, "throttle").intValue())
                .put("shieldsRaised", requiredBoolean(payload, "shieldsRaised"))
                .put("selectedTargetId", payload.get("selectedTargetId"));
        }

        return envelope(event.type().name(), event.sessionId().toString())
            .put("occurredAt", event.occurredAt().toString())
            .put("payload", new JsonObject(event.payload()));
    }

    public JsonObject serializeSnapshot(ShipState state) {
        Objects.requireNonNull(state, "state must not be null");

        return envelope("SHIP_STATE_SNAPSHOT", state.sessionId().toString())
            .put("tick", state.tick())
            .put("snapshotVersion", state.tick())
            .put("position", vector(state.position().x(), state.position().y(), state.position().z()))
            .put("velocity", vector(state.velocity().x(), state.velocity().y(), state.velocity().z()))
            .put("headingDegrees", state.headingDegrees())
            .put("throttle", state.throttle())
            .put("shieldsRaised", state.shieldsRaised())
            .put("selectedTargetId", state.selectedTargetId() == null ? null : state.selectedTargetId().toString());
    }

    private static JsonObject envelope(String type, String sessionId) {
        return new JsonObject()
            .put("protocolVersion", PROTOCOL_VERSION)
            .put("type", type)
            .put("sessionId", sessionId);
    }

    private static JsonObject vector(double x, double y, double z) {
        return new JsonObject().put("x", x).put("y", y).put("z", z);
    }

    private static long requiredLong(Map<String, Object> payload, String name) {
        return requiredNumber(payload, name).longValue();
    }

    private static boolean requiredBoolean(Map<String, Object> payload, String name) {
        Object value = payload.get(name);
        if (!(value instanceof Boolean result)) {
            throw new IllegalArgumentException("Missing or invalid replication field: " + name);
        }
        return result;
    }

    private static Number requiredNumber(Map<String, Object> payload, String name) {
        Object value = payload.get(name);
        if (!(value instanceof Number number)) {
            throw new IllegalArgumentException("Missing or invalid replication field: " + name);
        }
        return number;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> requiredMap(Map<String, Object> payload, String name) {
        Object value = payload.get(name);
        if (!(value instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException("Missing or invalid replication field: " + name);
        }
        return (Map<String, Object>) map;
    }
}
