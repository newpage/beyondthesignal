package com.beyondsignal.game.exploration;

import com.beyondsignal.game.sensor.SensorContact;
import com.beyondsignal.game.sensor.SensorScanResult;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ExplorationMap {
    private final Map<String, SensorContact> discoveredContacts = new LinkedHashMap<>();

    public synchronized void apply(SensorScanResult scanResult) {
        Objects.requireNonNull(scanResult, "scanResult");
        for (SensorContact contact : scanResult.contacts()) {
            SensorContact existing = discoveredContacts.get(contact.id());
            if (existing == null || betterThan(contact, existing)) {
                discoveredContacts.put(contact.id(), contact);
            }
        }
    }

    public synchronized Optional<SensorContact> contact(String id) {
        return Optional.ofNullable(discoveredContacts.get(id));
    }

    public synchronized Map<String, SensorContact> contacts() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(discoveredContacts));
    }

    public synchronized long identifiedCount() {
        return discoveredContacts.values().stream().filter(SensorContact::identified).count();
    }

    private static boolean betterThan(SensorContact candidate, SensorContact existing) {
        return (candidate.identified() && !existing.identified())
            || candidate.signalStrength() > existing.signalStrength();
    }
}
