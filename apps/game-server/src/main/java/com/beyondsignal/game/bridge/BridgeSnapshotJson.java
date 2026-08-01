package com.beyondsignal.game.bridge;

import java.util.Iterator;
import java.util.Map;

public final class BridgeSnapshotJson {
    public String serialize(BridgeState state) {
        StringBuilder json = new StringBuilder();
        json.append('{')
            .append("\"version\":").append(state.version()).append(',')
            .append("\"alertStatus\":\"").append(state.alertStatus()).append("\",")
            .append("\"headingDegrees\":").append(state.headingDegrees()).append(',')
            .append("\"throttle\":").append(state.throttle()).append(',')
            .append("\"shieldsRaised\":").append(state.shieldsRaised()).append(',')
            .append("\"shieldStrength\":").append(state.shieldStrength()).append(',')
            .append("\"hullIntegrity\":").append(state.hullIntegrity()).append(',')
            .append("\"torpedoesRemaining\":").append(state.torpedoesRemaining()).append(',')
            .append("\"selectedTargetId\":")
            .append(nullable(state.selectedTargetId())).append(',')
            .append("\"powerAllocations\":").append(enumMap(state.powerAllocations())).append(',')
            .append("\"crewAssignments\":").append(enumMap(state.crewAssignments())).append(',')
            .append("\"campaignTick\":").append(state.campaign().simulationTick())
            .append('}');
        return json.toString();
    }

    private static String nullable(String value) {
        return value == null ? "null" : "\"" + escape(value) + "\"";
    }

    private static String enumMap(Map<?, ?> values) {
        StringBuilder json = new StringBuilder("{");
        Iterator<? extends Map.Entry<?, ?>> iterator = values.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<?, ?> entry = iterator.next();
            json.append("\"").append(escape(entry.getKey().toString())).append("\":");
            Object value = entry.getValue();
            if (value instanceof Number || value instanceof Boolean) {
                json.append(value);
            } else {
                json.append("\"").append(escape(value.toString())).append("\"");
            }
            if (iterator.hasNext()) {
                json.append(',');
            }
        }
        return json.append('}').toString();
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
