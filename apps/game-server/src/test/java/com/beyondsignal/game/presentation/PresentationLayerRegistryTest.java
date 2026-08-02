package com.beyondsignal.game.presentation;

import static org.junit.jupiter.api.Assertions.*;
import com.beyondsignal.game.presentation.layers.*;
import org.junit.jupiter.api.Test;

class PresentationLayerRegistryTest {
    @Test
    void ordersLayersAndRejectsDuplicates() {
        PresentationLayerRegistry registry = PresentationLayerRegistry.defaults();
        assertEquals("grid", registry.orderedLayers().getFirst().id());
        assertEquals("debug", registry.orderedLayers().getLast().id());
        assertThrows(IllegalArgumentException.class,
            () -> registry.register(new PresentationLayer("grid", 1, true)));
    }
}
