package com.beyondsignal.game.presentation.layers;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class PresentationLayerRegistry {
    private final Map<String, PresentationLayer> layers = new LinkedHashMap<>();

    public synchronized void register(PresentationLayer layer) {
        Objects.requireNonNull(layer, "layer");
        if (layers.putIfAbsent(layer.id(), layer) != null) {
            throw new IllegalArgumentException("Layer already registered: " + layer.id());
        }
    }

    public synchronized List<PresentationLayer> orderedLayers() {
        return layers.values().stream()
            .sorted(Comparator.comparingInt(PresentationLayer::order)
                .thenComparing(PresentationLayer::id))
            .toList();
    }

    public static PresentationLayerRegistry defaults() {
        PresentationLayerRegistry registry = new PresentationLayerRegistry();
        registry.register(new PresentationLayer("grid", 10, true));
        registry.register(new PresentationLayer("formations", 20, true));
        registry.register(new PresentationLayer("movement", 30, true));
        registry.register(new PresentationLayer("ships", 40, true));
        registry.register(new PresentationLayer("labels", 50, true));
        registry.register(new PresentationLayer("debug", 90, false));
        return registry;
    }
}
