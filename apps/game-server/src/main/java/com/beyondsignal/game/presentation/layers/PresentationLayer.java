package com.beyondsignal.game.presentation.layers;

public record PresentationLayer(
    String id,
    int order,
    boolean enabledByDefault
) {
    public PresentationLayer {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id is required");
        if (order < 0) throw new IllegalArgumentException("order cannot be negative");
    }
}
