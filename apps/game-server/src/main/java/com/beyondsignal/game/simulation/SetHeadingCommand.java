package com.beyondsignal.game.simulation;

public record SetHeadingCommand(double headingDegrees) implements ShipCommand {
    public SetHeadingCommand {
        if (!Double.isFinite(headingDegrees)) {
            throw new IllegalArgumentException("headingDegrees must be finite");
        }
    }

    public double normalizedHeading() {
        double normalized = headingDegrees % 360.0;
        return normalized < 0.0 ? normalized + 360.0 : normalized;
    }
}
