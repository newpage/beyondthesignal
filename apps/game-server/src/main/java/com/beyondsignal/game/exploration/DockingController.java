package com.beyondsignal.game.exploration;

public final class DockingController {
    private DockingStatus status = DockingStatus.UNDOCKED;
    private String dockedSystemId;

    public synchronized void requestDocking(String systemId, boolean hasStation, double distance) {
        if (systemId == null || systemId.isBlank()) {
            throw new IllegalArgumentException("systemId is required");
        }
        if (!hasStation) {
            throw new IllegalStateException("No station is available");
        }
        if (!Double.isFinite(distance) || distance > 1.0) {
            throw new IllegalStateException("Ship is outside docking range");
        }
        status = DockingStatus.APPROACHING;
        dockedSystemId = systemId;
    }

    public synchronized void completeDocking() {
        if (status != DockingStatus.APPROACHING) {
            throw new IllegalStateException("Docking has not been requested");
        }
        status = DockingStatus.DOCKED;
    }

    public synchronized void undock() {
        status = DockingStatus.UNDOCKED;
        dockedSystemId = null;
    }

    public synchronized DockingStatus status() {
        return status;
    }

    public synchronized String dockedSystemId() {
        return dockedSystemId;
    }
}
